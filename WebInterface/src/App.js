import React,{Component } from 'react';
import { BrowserRouter, Route, Switch} from 'react-router-dom'
import Home from './components/Home/Home'
import Result from './components/Result/Result'
import countries from './countries'
import Images from './components/Trends/Images'
import Trends from './components/Trends/Trends'


class App extends Component {
  render(){
    return (
      <BrowserRouter>
      <div className="App">
        <Switch>
        <Route exact path='/' render={(props) => <Home {...props} items={countries} />}/>
        <Route  path="/results" component={Result} />
        <Route path="/trends" component={Trends} />
        <Route  path="/images" component={Images} />
        </Switch>
      </div>
      </BrowserRouter>
    );
  }
  }
  

export default App;
