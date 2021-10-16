# import numpy as np
# from flatland.envs.rail_env import RailEnv

# def search(env: RailEnv):

#     # Creates a schedule of 8 steps of random actions. 
#     schedule = []
#     for _ in range(0, 8):
#         _actions = {}
#         for i in env.get_agent_handles():
#             _actions[i] = np.random.randint(0, 5)
#         schedule.append(_actions)
    
#     return schedule


import copy
import numpy
import itertools
import heapq

from dataclasses import dataclass, field
from typing import Any

from flatland.core.grid.grid4_utils import get_new_position
from flatland.envs.rail_env import RailEnv, RailEnvActions

#from testmaps import two_agent_collision_map, random_square_map
from numba.core.decorators import njit

class StateConverter:

    def __init__(self, env: RailEnv):
        self.width = env.rail.width
        self.num_tiles = env.rail.width * env.rail.height
        self.num_states = 4 * self.num_tiles

        

    def position_to_state(self, row, col, dir):
        return dir + 4 * col + 4 * self.width * row

    def position_to_tile(self, position):
        return position[1] + self.width * position[0]

    def state_to_position(self, state):
        dir =   state % 4
        col = ((state - dir) / 4) % self.width
        row = ((state - dir - col * 4)) / (4 * self.width)
        return (row, col, dir)

    @staticmethod
    def state_to_tile(state):
        return numpy.int32((state - state % 4) / 4)


def convert_to_transition(env: RailEnv, conv: StateConverter):

    # Transition is a function: [state][action] -> new state
    transition = -numpy.ones((conv.num_states, 5), dtype=numpy.int32)

    # Action is valid in a particular state if it leads to a new position.
    valid_action = numpy.zeros((conv.num_states, 5), dtype=numpy.int32)

    for row in range(0, env.rail.height):
        for col in range(0, env.rail.width):
            for dir in range(0, 4):

                # Compute the current state index.
                state = conv.position_to_state(row, col, dir)

                # Compute the number of possible transitions.
                possible_transitions = env.rail.get_transitions(row, col, dir)
                num_transitions = numpy.count_nonzero(possible_transitions)

                if num_transitions > 0:

                    # The easy case: stop moving holds current state. 
                    transition[state][RailEnvActions.STOP_MOVING] = state
                    valid_action[state][RailEnvActions.STOP_MOVING] = 1
    
                    # Forward is only possible in two cases, there is only 1 option, or
                    # the current direction can be maintained. Stop otherwise.
                    if num_transitions == 1:
                        new_direction = numpy.argmax(possible_transitions)
                        new_position = get_new_position((row, col), new_direction)
                        transition[state][RailEnvActions.MOVE_FORWARD] = conv.position_to_state(new_position[0], new_position[1], new_direction)
                        valid_action[state][RailEnvActions.MOVE_FORWARD] = 1
                    elif possible_transitions[dir] == 1:
                        new_position = get_new_position((row, col), dir)
                        transition[state][RailEnvActions.MOVE_FORWARD] = conv.position_to_state(new_position[0], new_position[1], dir)
                        valid_action[state][RailEnvActions.MOVE_FORWARD] = 1
                    else:
                        transition[state][RailEnvActions.MOVE_FORWARD] = state

                    # Left is only possible if there is a transition out to the left of
                    # the current direction. Otherwise, we move like we would if going
                    # Forward.
                    new_direction = (dir - 1) % 4
                    if possible_transitions[new_direction]:
                        new_position = get_new_position((row, col), new_direction)
                        transition[state][RailEnvActions.MOVE_LEFT] = conv.position_to_state(new_position[0], new_position[1], new_direction)
                        valid_action[state][RailEnvActions.MOVE_LEFT] = transition[state][RailEnvActions.MOVE_LEFT] != transition[state][RailEnvActions.MOVE_FORWARD]
                    else:
                        transition[state][RailEnvActions.MOVE_LEFT] = transition[state][RailEnvActions.MOVE_FORWARD]

                    # Right is only possible if there is a transition out to the Right of
                    # the current direction. Otherwise, we move like we would if going
                    # Forward.
                    new_direction = (dir + 1) % 4
                    if possible_transitions[new_direction]:
                        new_position = get_new_position((row, col), new_direction)
                        transition[state][RailEnvActions.MOVE_RIGHT] = conv.position_to_state(new_position[0], new_position[1], new_direction)
                        valid_action[state][RailEnvActions.MOVE_RIGHT] = transition[state][RailEnvActions.MOVE_RIGHT] != transition[state][RailEnvActions.MOVE_FORWARD]
                    else:
                        transition[state][RailEnvActions.MOVE_RIGHT] = transition[state][RailEnvActions.MOVE_FORWARD]

    return (transition, valid_action)

@njit
def all_pairs_shortest_paths(num_states, transition):

    dist = numpy.ones((num_states, num_states), dtype=numpy.int32) * numpy.inf

    # Initialize; neighbors of the current state are at distance 1 step, current state at 0 steps.
    for state in range(0, num_states):
        for action in range(1, 4):
            next_state = transition[state][action]
            if next_state != -1 and next_state != state:
                dist[state][next_state] = 1
        dist[state][state] = 0

    # FLoyd-Warshall algorithm to compute distances of shortest paths.
    for k in range(0, num_states):
        for i in range(0, num_states):
            for j in range(0, num_states):
                if dist[i][j] > dist[i][k] + dist[k][j]:
                    dist[i][j] = dist[i][k] + dist[k][j]

    return dist

class SearchEnv:

    def __init__(self, env: RailEnv, train_id = None, occor_map = None):
        '''
        changes to make for single agent
        '''
        
        if train_id != None:
            self.train_id = train_id
            self.agent = env.agents[train_id] 
            env.agents = [env.agents[train_id]]
            self.time = env._elapsed_steps
            self.rd = env.agents[0].release_date
            self.deadline = env.agents[0].deadline
            
            # print ("t:", self.time, "rd:",self.rd) 
        
        self.occor_map = occor_map


        self.max_step = env._max_episode_steps
        self.conv = StateConverter(env)
        model = convert_to_transition(env, self.conv)
        self.transition = model[0]
        self.valid_actions = model[1]
        self.shortest = all_pairs_shortest_paths(self.conv.num_states, self.transition)

        self.initial_state = numpy.zeros(len(env.agents), dtype=numpy.int32)
        self.initial_active = numpy.zeros(len(env.agents), dtype=numpy.int32)
        for i in range(0, len(env.agents)):
            agent = env.agents[i]
            self.initial_state[i] = self.conv.position_to_state(agent.initial_position[0], agent.initial_position[1], agent.initial_direction)

        self.goal_tile = numpy.zeros(len(env.agents), dtype=numpy.int32)
        for i in range(0, len(env.agents)):
            self.goal_tile[i] = self.conv.position_to_tile(env.agents[i].target)

        # Convert from tiles to states by adding directions 0 to 4.
        self.goal_states = numpy.mgrid[0:len(env.agents),0:4][1] + self.goal_tile.reshape(len(env.agents),1) * 4

    def get_root_node(self):
        initial_state = SearchState(self.initial_state.copy(), self.initial_active.copy())
        # if self.occor_map != None:
        #     return SearchNode(0, None, None, self, initial_state)
        return SearchNode(0, None, None, self, initial_state)

class SearchState:
    def __init__(self, positions, actives):
        self.positions = positions
        self.actives = actives
        self.hash = hash(self.actives.tobytes()) + 31 * hash(self.positions.tobytes())

    def __hash__(self):
        return self.hash

    def __eq__(self, other):
        if isinstance(other, self.__class__):
            return numpy.array_equal(self.actives, other.actives) and numpy.array_equal(self.positions, other.positions)
        else:
            return NotImplemented

@dataclass(order=True)
class SearchNode:

    f: int 
    neg_g: int

    parent: Any=field(compare=False)
    action: Any=field(compare=False)
    searchenv: Any=field(compare=False)
    searchstate: Any=field(compare=False)
    time : Any = field(compare=False)

    def __init__(self, neg_g, parent, action, searchenv, searchstate):

        self.parent = parent
        self.action = action
        self.searchenv = searchenv
        self.searchstate = searchstate
        self.neg_g = neg_g


        if parent != None: 
            self.time = self.parent.time + 1
        else:
            self.time = 0

        if self.time <= searchenv.rd and action == (2,):
            neg_g = -99999
            # print(self.action, searchenv.rd, self.time,  "true")
        else:
            neg_g = neg_g
            # print(self.action, searchenv.rd, self.time, "false")
        
        self.f = self.get_evaluation()

        
        if searchenv.occor_map != None:
            try:
                self.occu_map= copy.deepcopy(searchenv.occor_map[self.time])
            except IndexError:
                self.occu_map = None
        else:
            self.occu_map = None
    

        if self.occu_map is not None:
            tiles = self.searchenv.conv.state_to_tile(self.searchstate.positions)
            valid_tiles = tiles[self.searchstate.actives == 1]
            self.occu_map[valid_tiles] = 1

        else:
            self.occu_map = numpy.zeros(self.searchenv.conv.num_tiles)
            tiles = self.searchenv.conv.state_to_tile(self.searchstate.positions)
            valid_tiles = tiles[self.searchstate.actives == 1]
            self.occu_map[valid_tiles] = 1
        
        # self.combine_occu_map(self.parent)
        


    # def __lt__ (self, other):
    #     return self.get_evaluation() < other.get_evaluation()


    def combine_occu_map(self, other):
        new_map = numpy.zeros(self.searchenv.conv.num_tiles)
        
        for i in range(len(self.occu_map)):
            if self.occu_map[i] == 1 or other.occu_map[i] == 1:
                new_map[i] = 1
        
        return new_map

    def __hash__(self):
        return self.searchstate.__hash__()

    def __eq__(self, other):
        if isinstance(other, self.__class__):
            return self.searchstate.__eq__(other.searchstate)
        else:
            return NotImplemented

    def agents_at_goal(self):
        """
        Check if the current state has all the agents at one of the goal states.
        """
        return self.searchenv.conv.state_to_tile(self.searchstate.positions) == self.searchenv.goal_tile

    def is_goal_state(self):
        return self.agents_at_goal().all()

    def get_evaluation(self):
        if self.action != (0,) and (self.time <= self.searchenv.rd):
            return -self.neg_g + self.get_heuristic() + ((self.searchenv.rd - self.time)**2 / (0.25 * self.searchenv.max_step) + 1) * 4

        if not self.agents_at_goal() and self.time > self.searchenv.deadline:
            return -self.neg_g + self.get_heuristic() +((self.time - self.searchenv.deadline)**2 / (0.25 * self.searchenv.max_step) + 1) * 4

        return -self.neg_g + self.get_heuristic()

    def get_heuristic(self):
        shortest_to_goal_states = self.searchenv.shortest[self.searchstate.positions.reshape(len(self.searchstate.positions),1), self.searchenv.goal_states]
        shortest_to_goal_state = numpy.min(shortest_to_goal_states, 1)
        return numpy.int32(numpy.max(shortest_to_goal_state))

    def get_occupied_tiles(self):
        return self.occu_map

        occupied = numpy.zeros(self.searchenv.conv.num_tiles)
        tiles = self.searchenv.conv.state_to_tile(self.searchstate.positions)
        valid_tiles = tiles[self.searchstate.actives == 1]
        occupied[valid_tiles] = 1
        return occupied

    def get_all_valid_actions(self):

        # Select, for each agent, the valid actions based on its position.
        agent_actions = self.searchenv.valid_actions[self.searchstate.positions]

        # Mask the rail transition actions for idle agents.
        agent_actions[self.searchstate.actives == 0] = [1, 0, 1, 0, 0]     # DO_NOTHING, or MOVE_FORWARD.

        # Mask the rail transition actions for done agents.
        agent_actions[self.agents_at_goal()] = [1, 0, 0, 0, 0]     # DO_NOTHING only.

        # Identify for each agent the IDs of the valid actions (i.e., [0, 1, 1, 0, 0] --> [1, 2])
        agent_action_list = [ numpy.nonzero(a)[0] for a in agent_actions ]

        # Return list containing for each agent, the IDs of the actions available to it.
        return itertools.product(*agent_action_list)

    def expand_node(self, actions):

        """
        Input:
         - actions: an array, where actions[agent] is the action id that agent id will try to take.
        """

        # Determine which tiles are occupied now.
        occupied = self.get_occupied_tiles()
        #print ("*", occupied)

        # Make copy the current search state (to modify).
        new_states = self.searchstate.positions.copy()
        new_actives = self.searchstate.actives.copy()

        # Move agents in increasing order of their IDs.
        for i in range(0, len(self.searchstate.positions)):

            # Get the current state of the agent.
            current_state = new_states[i]
            current_tile = self.searchenv.conv.state_to_tile(current_state)

            # Agent was inactive, wants to begin moving.
            if new_actives[i] == 0 and actions[i] == 2:
                if occupied[current_tile] == 1:
                    # Attempting to enter blocked tile, expand fails.
                    return None
                else:
                    # Activate agent, occupy tile.
                    new_actives[i] = 1
                    occupied[current_tile] = 1

            # Agent was active, attempt to apply action
            elif new_actives[i] == 1:

                # The agent is trying to move, so it frees up the current tile.
                occupied[current_tile] = 0

                next_state = self.searchenv.transition[current_state, actions[i]]
                next_tile = self.searchenv.conv.state_to_tile(next_state)
                if occupied[next_tile] == 1:
                    # Attempting to enter blocked tile, expand fails.
                    return None
                else:
                    occupied[current_tile] = 0
                    occupied[next_tile] = 1
                    new_states[i] = next_state

                    # Goal state reached, remove the occupancy, deactivate.
                    if next_tile == self.searchenv.goal_tile[i]:
                        occupied[next_tile] = 0
                        new_actives[i] = 0

        return SearchNode(self.neg_g - 1, self, actions, self.searchenv, SearchState(new_states, new_actives))

    def get_path(self):
        action_dict = dict(enumerate(self.action))
        if self.parent.parent is None:
            return [action_dict]
        else:
            path = self.parent.get_path()
            path.append(action_dict)
            return path
    
    def get_occu_map(self):
        occu_dict = self.occu_map
        if self.parent.parent is None:
            return [occu_dict]
        else:
            occu = self.parent.get_occu_map()
            occu.append(occu_dict)
            return occu

def a_star_search(root):

    # Count the number of expansions and generations.
    expansions = 0
    generations = 0

    # Open list is a priority queue over search nodes, closed set is a hash-based set for tracking seen nodes.
    openlist = []
    closed = set() # {root}

    # Initially, open list is just the root node.
    heapq.heappush(openlist, root)
    # heapq.heappush()

    iter = 0
    # While we have candidates to expand, 
    while len(openlist) > 0 and iter < 100:

        # Get the highest priority search node.
        current = heapq.heappop(openlist)
        
        # print ("-----------")
        # print("current time", current.time, "rd:", current.searchenv.rd, "d: ", current.searchenv.deadline)
        # print ("eval:",current.get_evaluation())
        # print ("chosen step:", current.action)
        # print ("-----------")

        # Increment number of expansions.
        expansions = expansions + 1

        # If we expand the goal node, we are done.
        if current.is_goal_state():
            return (current.get_path(), current.get_occu_map(), expansions, generations)

  
        # Otherwise, we will generate all child nodes.
        for action in current.get_all_valid_actions():
    
            
            # Create successor node from action.
            nextnode = current.expand_node(action)
            
            # print (action)
            # print (nextnode == None)

            # Generated one more node.
            generations = generations + 1

        
            if current.time <= current.searchenv.rd:
                if nextnode is not None and not closed.__contains__(nextnode):
                    heapq.heappush(openlist, nextnode)
                    # closed.add(nextnode)
            # If this is a valid new node, append it to the open list.
            else:
                if nextnode is not None and not closed.__contains__(nextnode):
                    heapq.heappush(openlist, nextnode)
                    closed.add(nextnode)
        
        iter += 1
    #print("stop", len(openlist), current.is_goal_state())
    return(current.get_path(), current.get_occu_map(), expansions, generations)
    return (None, expansions, generations)

def search_stats(env:RailEnv, num= None, occor_map = None):
    if num == None : 
        return a_star_search(SearchEnv(env).get_root_node())
    return a_star_search(SearchEnv(env,num, occor_map).get_root_node())

def search(env: RailEnv):
    #return search_stats(copy.deepcopy(env))[0]
    
    #create new occ map
    occu_map = []
    
    id = list_of_id(env)

    # print(id)

    for i in range(env.width):
        occu_map.append([])
        for _ in range(env.width):
            occu_map[i].append(0)
    
    ways = search_stats(copy.deepcopy(env), id[0][0])
    # print("id:", 0, "rd:", env.agents[0].release_date, "d:", env.agents[0].deadline)
    occu_map = ways[1]
    final2 = [ways[0]]
  
    for train_id in range (1, len(env.agents)):
        ways = search_stats(copy.deepcopy(env), id[train_id][0], occu_map)
        # print(id[train_id][0])
        # print("id:", train_id, "rd:", env.agents[train_id].release_date, "d:", env.agents[train_id].deadline)
        occu_map = ways[1]

        final2.append(ways[0])

    # print (occu_map)


    return (combine(final2, id))

def list_of_id(env:RailEnv):
    a = []
    for id, agent in enumerate (env.agents):
        a.append((id, agent.release_date))
    
    return sorted(a, key= take_rd)
    
def take_rd (a):
    return a[1]


def combine (list_of_action, list_of_id):
    max = len(list_of_action[0])
    for lst in list_of_action:
        if len(lst) > max:
            max = len(lst)
    
    result = []

    for iter in range(max):
        i = 0
        temp = {}
        for list1 in list_of_action:
            if iter >= len(list1):
                pass
            elif list1[iter][0] == 0:
                pass
            else:
                temp[list_of_id[i][0]] = list1[iter][0]
            i+= 1
        result.append(temp)
    return result
