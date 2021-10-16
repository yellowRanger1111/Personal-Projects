import time
import threading

import numpy
import pyglet

from flatland.utils.rendertools import RenderTool
from flatland.utils.graphics_pgl import RailViewWindow

from utils.environment_utils import create_multi_agent_environment
from q3_improved_q1 import search

# Evaluates the A* search algorithm over a number of samples.
def evalfun(num_samples = 100, timed=True, debug = False, refresh = 0.1):

    # A list of (mapsize, agent count) tuples, change or extend this to test different sizes.
    problemsizes = [(25,1)]
    #[(5, 3), (7, 4), (9, 5), (11, 6), (13, 7)]

    # Create a list of seeds to consider.
    seeds = 37429879
    # numpy.random.randint(2**29, size=3*num_samples)
    
    print("%10s\t%8s\t%8s\t%9s" % ("Dimensions", "Success", "Rewards", "Runtime"))
    for problemsize in problemsizes:
        j = 0
        for _ in range(0, 1):

            # Create environments while they are not the intended dimension.
            env = create_multi_agent_environment(problemsize[0], problemsize[1], timed, seeds)
            j = j + 1
            while len(env.agents) != problemsize[1]:
                env = create_multi_agent_environment(problemsize[0], problemsize[1], timed, seeds)
                j = j + 1

            # Create a renderer only if in debug mode.
            if debug:
                env_renderer = RenderTool(env, screen_width=1920, screen_height=1080)


            #print(env.agents)

            # Time the search.
            start = time.time()
            schedule = search(env)

            # print(schedule)

            duration = time.time() - start;

            if debug:
                env_renderer.render_env(show=True, frames=False, show_observations=False)
                time.sleep(refresh)
        
            # Validate that environment state is unchanged.
            assert env.num_resets == 1 and env._elapsed_steps == 0
        
            # Run the schedule
            success = False
            sumreward = 0
            for action in schedule:
                _, _reward_dict, _done, _ = env.step(action)
                success = all(_done.values())
                sumreward = sumreward + sum(_reward_dict.values())
                if debug:
                    #print(action)
                    env_renderer.render_env(show=True, frames=False, show_observations=False)
                    time.sleep(refresh)
        
            # Print the performance of the algorithm
            print("%10s\t%8s\t%8.3f\t%9.6f" % (str(problemsize), str(success), sumreward, duration))


if __name__ == "__main__":

    # Number of maps of each size to consider.
    _num_maps = 5
    # If _timed = true, impose release dates and deadlines. False for regular (Assignment 1) behavior.
    _timed = True

    _debug = True
    _refresh = 0.3

    if (_debug):
        window = RailViewWindow()

    evalthread = threading.Thread(target=evalfun, args=(_num_maps,_timed,_debug,_refresh,))
    evalthread.start()

    if (_debug):
        pyglet.clock.schedule_interval(window.update_texture, 1/120.0)
        pyglet.app.run()

    evalthread.join()
