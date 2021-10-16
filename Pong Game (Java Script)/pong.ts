// Pong Game 
// By : Owen Austin Oei (ooei0002@student.monash.edu)

import { interval, fromEvent, from, zip } from 'rxjs'
import { map, scan, filter, merge, flatMap, take, concat} from 'rxjs/operators'


//copied from https://tgdwyer.github.io/asteroids/ 
class Vec {
  constructor(public readonly x: number = 0, public readonly y: number = 0) {}
  add = (b:Vec) => new Vec(this.x + b.x, this.y + b.y)
  sub = (b:Vec) => this.add(b.scale(-1))
  len = ()=> Math.sqrt(this.x*this.x + this.y*this.y)
  scale = (s:number) => new Vec(this.x*s,this.y*s)
  ortho = ()=> new Vec(this.y,-this.x)
  rotate = (deg:number) =>
            (rad =>(
                (cos,sin,{x,y})=>new Vec(x*cos - y*sin, x*sin + y*cos)
              )(Math.cos(rad), Math.sin(rad), this)
            )(Math.PI * deg / 180)

  static unitVecInDirection = (deg: number) => new Vec(0,-1).rotate(deg)
  static Zero = new Vec();
}

//to normally set attribute
const apply = (dy) => (rect) => {
  rect.setAttribute('y', String(dy + Number(rect.getAttribute('y'))));
}


//some usefull variable
const CanvasSizeY = 600,
      CanvasSizeX = 1000,
      PaddleWidth = 15,
      paddlelength = 200,
      maxScore = 2;

//function for making sur the paddle stays on the grid
const blockexit = (v:number) => v < 0 ? 0 : v + paddlelength > CanvasSizeY? CanvasSizeY - paddlelength: v;

const torusWrap = ({x,y}:Vec) => { 
  return new Vec(wrap(x),wrap(y))
};

const wrap = (v:number) => v < 0 ? v + CanvasSizeY : v > CanvasSizeY ? v - CanvasSizeY : v;


//types to be used
type Event = 'keydown' | 'keyup'
type Key = 'ArrowDown' | 'ArrowUp' | "KeyW" | "KeyS" | "ArrowLeft" | "KeyA"

//classes used
class Tick { constructor(public readonly elapsed:number) {} }
class Move { constructor(public readonly direction:number) {} }
class Move2 { constructor (public readonly direction: number) {} }
class ultimate1{ constructor(public readonly doit: boolean)  {} }
class ultimate2 { constructor(public readonly doit: boolean) {} }


function pong( twoPyr:boolean ) {
    // Inside this function you will use the classes and functions 
    // from rx.js
    // to add visuals to the svg element in pong.html, animate them, and make them interactive.
    // Study and complete the tasks in observable exampels first to get ideas.
    // Course Notes showing Asteroids in FRP: https://tgdwyer.github.io/asteroids/ 
    // You will be marked on your functional programming style
    // as well as the functionality that you implement.
    // Document your code!  
    
    //testing function
    const a = function(a){
      console.log(a)
      return a
    }

    //making observable streams
    const keyObservable = <T>(e:Event, k:Key, result:()=>T)=>
          fromEvent<KeyboardEvent>(document,e)
          .pipe(
          //map(a),
          filter(({code})=>code === k),
          map(result)),
          startGoUp = keyObservable("keydown", "ArrowUp", ()=> new Move2(-20)),
          startGoDown = keyObservable("keydown", "ArrowDown", ()=> new Move2(20)),
          stopGoUp = keyObservable("keyup", "ArrowUp", ()=> new Move2(0)),
          stopGoDown = keyObservable("keyup", "ArrowDown", ()=> new Move2(0)),
          ult2 = keyObservable("keydown", "ArrowLeft", () => new ultimate2(true)),
    
          // for second player
          GoUp = keyObservable("keydown", 'KeyW', ()=> new Move(-20)),
          GoDown = keyObservable("keydown", 'KeyS', ()=> new Move(20)),
          stoGoUp = keyObservable("keyup", 'KeyW', ()=> new Move(0)),
          stoGoDown = keyObservable("keyup", 'KeyS', ()=> new Move(0)),
          ult1 = keyObservable("keydown", "KeyA", () => new ultimate1(true))

    //type for every object
    type Body = Readonly<{
      id:string,
      pos:Vec, 
      vel:Vec,
      acc : Vec,
      createTime:number,
      ult_stat: boolean
    }>
    //type state, for every tick, a state is the current state and need to be updated every interval
    type State = Readonly<{
      time:number,
      paddle:Body,
      ball:Body,
      comp: Body,
      exit:ReadonlyArray<Body>,
      scrPyr: number,
      scrCmp:number,
      gameOver:boolean
    }>

    //base function to create paddle
    function createPaddle(bol:boolean):Body{
      return {
        id : "rect1",
        pos: new Vec(20, CanvasSizeY/3),
        vel:Vec.Zero,
        acc: Vec.Zero,
        createTime:0,
        ult_stat: bol
      }
    }
    //base function to create the second paddle
    function createComp(bol:boolean):Body{
      return {
        id : "rect2",
        pos: new Vec(960, CanvasSizeY/3),
        vel:Vec.Zero,
        acc: Vec.Zero,
        createTime:0,
        ult_stat: bol
      }
    }

    //base function to create the ball
    function createBall():Body {
      return{
      id : "ball",
      pos: new Vec(CanvasSizeX/2, CanvasSizeY/2),
      vel: new Vec(0.25 , 0.5 - Math.random()).scale(4),
      acc: Vec.Zero,
      createTime: 0,
      ult_stat:false
      }
    }

    //this is the function to make the ball bounce of the wall
    const bounce  = (b:Body):Body =>(b.pos.y > CanvasSizeY|| b.pos.y < 0)?
      {...b, vel: new Vec(b.vel.x, -(b.vel.y)), acc: new Vec(b.vel.x, -(b.vel.y)).scale(0.0000001)}
      :{...b}

    //this function is to bounce the ball of the paddle
    const hitPadlle = (s: State) : State => Math.floor(s.ball.pos.x) < s.paddle.pos.x + PaddleWidth && Math.floor(s.ball.pos.x) > s.paddle.pos.x  && s.ball.pos.y >= s.paddle.pos.y && s.ball.pos.y <= s.paddle.pos.y + paddlelength?
      {...s, ball: {...s.ball, vel: new Vec(-s.ball.vel.x, (-0.75 + (s.ball.pos.y-s.paddle.pos.y)/(paddlelength/2))).scale(1.1), acc: new Vec(-s.ball.vel.x, 0).scale(0.0000001)}}
      : Math.floor(s.ball.pos.x) < s.comp.pos.x + PaddleWidth && Math.floor(s.ball.pos.x) > s.comp.pos.x&& s.ball.pos.y >= s.comp.pos.y && s.ball.pos.y <= s.comp.pos.y + paddlelength?
      {...s, ball: {...s.ball, vel: new Vec(-s.ball.vel.x, (-0.75 + (s.ball.pos.y-s.comp.pos.y)/(paddlelength/2))).scale(1.1), acc: new Vec(-s.ball.vel.x, 0).scale(0.0000001)}}
      :{...s}

 
    //this function to increment the score if the ball crosses the goal
    const score = (s:State): State => s.ball.pos.x < 0? reset({...s, scrCmp: s.scrCmp + 1})
    :s.ball.pos.x > CanvasSizeX? reset({...s, scrPyr: s.scrPyr + 1})
    :{...s}
    
    //this function to resets the ball after the goal
    const reset = (s:State)  => <State>{
      ...s,
      //paddle : createPaddle(s.paddle.ult_stat),
      ball : createBall()
      //comp : createComp(s.comp.ult_stat),
    }

    //the first state
    const initialState:State= {
      time: 0,
      paddle : createPaddle(true),
      ball : createBall(),
      comp : createComp(twoPyr),
      exit: [],
      scrCmp: 0,
      scrPyr:0,
      gameOver: false
    }

    //to create an ult if after 20 second used
    const ultStat = (s:State) : State => s.paddle.ult_stat == false? s.time - s.paddle.createTime  >= 2000? {...s, paddle :{...s.paddle, ult_stat: true}} : {...s} 
    :s.comp.ult_stat == false? s.time - s.comp.createTime >= 2000? {...s, comp:{...s.comp, ult_stat:true}}: {...s} : {...s}

    //if one wins 
    const gameEnd = (s:State):State => s.scrCmp == maxScore? {...s, gameOver: true}:
    s.scrPyr == maxScore? {...s, gameOver:true}:
    {...s}

    //to move the ball every turn
    const moveObj = (o:Body) => <Body>{
      ...o,
      pos:o.pos.add(o.vel),
      vel:o.vel.add(o.acc)
    }

    //function to do the ultimate for the first player
    const doUltimate1 = (s:State) => s.paddle.ult_stat? {
      ...s, 
      ball: {...s.ball, pos: new Vec(800, Math.random()*CanvasSizeY), vel : new Vec (2, 0)},
      paddle: {...s.paddle, ult_stat : false, createTime : s.time}
    }
    :{...s} 

    //function to do the ultimate for the second player
    const doUltimate2 = (s:State) => s.comp.ult_stat? {
      ...s, 
      ball: {...s.ball, pos: new Vec(200, Math.random()*CanvasSizeY), vel : new Vec (-2, 0)},
      comp: {...s.comp, ult_stat:false, createTime: s.time}
    }
    :{...s} 

    //to make the computer to follow the ball
    const followBall = (ball:Body) => (comp:Body) => ball.pos.y < comp.pos.y + 10? {...comp, pos : new Vec(comp.pos.x, blockexit(comp.pos.y - 1))}
    : ball.pos.y> (comp.pos.y + paddlelength - 10)? {...comp, pos : new Vec (comp.pos.x, blockexit(comp.pos.y + 1))} 
    : {...comp}

    //the back end function to change the state
    const reduceState = (s:State, e:Move| Tick | Move2 | ultimate1| ultimate2)=>
    e instanceof Move? {...s,
      paddle : {
        ...s.paddle,
        pos: new Vec (s.paddle.pos.x, blockexit(s.paddle.pos.y + e.direction))
      }   
    } : e instanceof ultimate1?
      doUltimate1(s)
    : twoPyr && e instanceof ultimate2?
      doUltimate2(s)
    : twoPyr && e instanceof Move2?{
      ...s,
      comp :{
        ...s.comp,
        pos : new Vec (s.comp.pos.x, blockexit(s.comp.pos.y + e.direction))
      }
    }
    : twoPyr?
      ultStat(
        gameEnd(
          score( 
            hitPadlle({
              ...s , 
              ball: bounce(moveObj(s.ball)),
              time: s.time+1
            }))))
    : ultStat(
      gameEnd(
        score(
          hitPadlle({
            ...s , 
            ball: bounce(moveObj(s.ball)),
            comp: followBall(s.ball) (s.comp),
            time: s.time+1
          }
    ))));


    //the function that deals with the graphics
    function updateView(s: State) {
      const 
        svg = document.getElementById("canvas")!,
        paddle = document.getElementById("rect1")!,
        ball = document.getElementById("ball")!,
        comp = document.getElementById("rect2")!,
        
        attr = (e:Element,o:Object) =>
          { for(const k in o) e.setAttribute(k,String(o[k])) }
      
      attr(paddle,{transform:`translate(${s.paddle.pos.x},${s.paddle.pos.y})`});
      attr(ball, {transform:`translate(${s.ball.pos.x},${s.ball.pos.y})`})
      attr(comp,{transform:`translate(${s.comp.pos.x},${s.comp.pos.y})`})

      const v = document.getElementById("scrPyr")!;
      const l = document.getElementById("scrCmp")!;
      v.innerHTML = s.scrPyr.toString();
      l.innerHTML = s.scrCmp.toString();
      
      const k = document.getElementById("ultStat1")!;
      const j = document.getElementById("ultStat2")!;
      s.paddle.ult_stat?k.innerHTML = "Ready":k.innerHTML = "Ready in " + Math.floor((2000 - (s.time - s.paddle.createTime))/100).toString() + "s"
      twoPyr?s.comp.ult_stat? j.innerHTML ="Ready": j.innerHTML = "Ready in " + Math.floor((2000 - (s.time - s.comp.createTime))/100).toString() + "s": j.innerHTML = "Not Ready"


      if (s.gameOver){
        subcription.unsubscribe()
        const v = document.createElementNS(svg.namespaceURI, "text")!;
        attr(v,{x:CanvasSizeX/3,y:CanvasSizeY/2,class:"gameover"});
        twoPyr?s.scrPyr==maxScore?v.innerHTML = "P1 Wins":v.innerHTML = "P2 Wins":s.scrPyr==maxScore? v.innerHTML = "You Win" : v.innerHTML = "You Lose"
        svg.appendChild(v);
      }
    }

    //main stream
    const subcription = interval(10).pipe(
      map(elapsed=>new Tick(elapsed)),
      merge(startGoUp, startGoDown, stopGoDown, stopGoUp),
      merge(GoDown, GoUp, stoGoDown,startGoUp),
      merge(ult1,ult2),
      scan(reduceState, initialState)
      ).subscribe(updateView);
  
  }
  
  // the following simply runs your pong function on window load.  Make sure to leave it in place.
  if (typeof window != 'undefined')
    window.onload = ()=>{
      const onePLayerButton = document.getElementById("OnePlayer");
      onePLayerButton.onclick = function(){ 
        pong(false);
      }
      const twoPlayerButton = document.getElementById("TwoPlayer");
      twoPlayerButton.onclick= function(){
        pong(true)
      }
      const resetButton = document.getElementById("Reset");
      resetButton.onclick = () =>window.location.reload();
    }
  
  

