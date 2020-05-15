import React,{Component } from 'react';
import { BrowserRouter,Route,Switch} from 'react-router-dom'
import Home from './components/Home'
import Result from './components/Result'
import countries from './countries'
import Mic from './components/Mic'
//import SpeechRecognition from 'react-speech-recognition'

class App extends Component {
  render(){
    return (
      <BrowserRouter>
      <div className="App">
        <Switch>
        <Route exact path='/' render={(props) => <Home {...props} items={countries} />}/>
        <Route path ="/searchbyvoice" component={Mic} />
        <Route path="/results" component={Result} />
        </Switch>
      </div>
      </BrowserRouter>
    );
  }
  }
  

export default App;
