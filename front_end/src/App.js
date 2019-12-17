import React, { Component } from 'react';
import { Form, Button } from 'react-bootstrap';

import Sim from './components/Sim';
import Controls from './components/Controls';

import './css/App.css';

class App extends Component {
  constructor(props) {
    super(props)
    this.state = {
      curTime: 0,
      lawnSize: 30,
      sim: [],
      testCase: '',
      done: false
    };

    this.summary = this.summary.bind(this)
    this.handleChange = this.handleChange.bind(this)
    this.handleSubmit = this.handleSubmit.bind(this)
    this.handleNext = this.handleNext.bind(this)
    this.handleStop = this.handleStop.bind(this)
    this.handleFastForward = this.handleFastForward.bind(this)
  }

  handleChange(event) {
    console.log(event.target.value)
    this.setState({testCase: event.target.value})
  }

  async handleSubmit(event) {
    event.preventDefault()

    const URL = 'http://localhost:8080/osmowsis/start?inputFile=' + this.state.testCase;
    let res1  = await fetch(URL)
    console.log("simulation done")

    fetch('http://localhost:8080/osmowsis/report')
      .then(res => res.json())
      .then(data =>  {
        var steps = []
        for (var k of Object.keys(data.stepsInfo)) {
          steps.push(data.stepsInfo[k])
        }
        this.setState({
          sim: steps,
          done: false,
          lawnSize: data.lawnSize
        })
      })
  }

  summary() {
    var cutGrass = 0;
    let grassLeft = 0;
    let lawns = this.state.sim[this.state.curTime].mowersAfterActionLawnState
    let lawn = []

    for (var k of Object.keys(lawns)) {
      lawn.push(lawns[k])
    }

    lawn = lawn[lawn.length-1] ? lawn[lawn.length-1] : []

    for (let i = 0; i < lawn.length; i++) {
      for (let j = 0; j < lawn[i].length; j++) {
        if (lawn[i][j] == "grass") {
          grassLeft += 1
        } else if(lawn[i][j] == "empty") {
          cutGrass += 1
        }
      }
    }

    alert(
      "Simulation Done! \n" +
      "Lawn Size: " + this.state.lawnSize + "\n" +
      "Grass Cut: " + cutGrass + "\n" +
      "Grass Left: " + grassLeft + "\n" +
      "Turns Taken: " + this.state.curTime
    );
  }

  nextTimeStep() {
    if (this.state.curTime < this.state.sim.length-1) {
      const nxtTime = this.state.curTime + 1
      this.setState({curTime: nxtTime})
    } else if (this.state.curTime === this.state.sim.length-1) {
      this.summary()
      this.setState({
        done: true,
        sim: [],
        curTime: 0
      })
    } else {
      this.setState({
        done: true,
        sim: [],
        curTime: 0
      })
    }
  }

  handleNext(event) {
    event.preventDefault()
    this.nextTimeStep()
  }

  handleStop(event) {
    event.preventDefault()
    this.summary()
    this.setState({
      done: true,
      sim: [],
      curTime: 0
    })
  }

  fastForward() {
    if(!this.state.done){
      setTimeout(function(){
        this.nextTimeStep();
        this.fastForward();
      }.bind(this), 200);
    }
    return
  }

  handleFastForward(event) {
    event.persist()
    this.fastForward()
  }

  mowerActions() {
    let actions = []
    if (this.state.sim.length > 0) {
      for (var k of Object.keys(this.state.sim[this.state.curTime].mowersActions)){
        actions.push(this.state.sim[this.state.curTime].mowersActions[k])
      }
    }

    return actions
  }

  render() {
    const lawn = this.state.sim[this.state.curTime] ? this.state.sim[this.state.curTime].mowersAfterActionLawnState : []
    const actions = this.mowerActions().map(
      (i,k) => <p><b>Mower {k}:</b> {i}</p>
    )

    return (
      <div className="App">
        <div class="container">
          <h3>OsMowSis: Group 35</h3>
          <Sim lawn={lawn} />
          <Form onSubmit={this.handleSubmit}>
            <Form.Group controlId="exampleForm.ControlTextarea1">
              <Form.Label>Test Case Input</Form.Label>
              <Form.Control as="textarea" rows="3" onChange={this.handleChange} />
            </Form.Group>
            <Button variant="primary" type="submit">Submit Test Case</Button>
          </Form>
          <br />
          <Controls
            handleNext={this.handleNext}
            handleFastForward={this.handleFastForward}
            handleStop={this.handleStop} 
          />
          <br />
          {actions}
        </div>
      </div>
    );
  }
}

export default App;
