import React, {Component } from 'react'
import {Link} from 'react-router-dom';
import '../Home.css'
import Silogo from '../silogo.png'
//import lib from 'react-speech-recognition';
//import Mic from './Mic'
class Home extends Component{
  constructor(props){
    super(props);
    this.state={
      query:'',
      suggestions:[]
  }
  }
  handleChange=(e)=>{
    const{items}=this.props;
    const query = e.target.value;
    let suggestions=[];
    if(query.length>0){
      const regex =new RegExp(`^${query}`,`i`);
      suggestions = items.sort().filter(v => regex.test(v));
    }
    this.setState(()=>({suggestions,query: query})); 
  }
  suggestionSelected=(query)=>{
    this.setState(()=>({query:query,suggestions: []})); 
  }
  renderSuggestions(){
    const {suggestions} =this.state
    if(suggestions.length === 0){
      return null
  }
  return(
    <ul>
      {suggestions.map(item =>{
        return (<li onClick={() => this.suggestionSelected(item)}>{item}</li>) })}
    </ul>
  )
  }
    render(){
      const {query} = this.state
      console.log(this.props)
        return (
          <div className="container">
            <img src={Silogo} alt="Searchy" />
              <div className="AutoCompleteText">
              <input onChange={this.handleChange} value={query} type="text"  class="form-control form-control-sm ml-3 w-100" id="search-input" placeholder="Search..."/>
              <div>{this.renderSuggestions()}</div>
              </div>
            <div className="center">
            <Link to="/results" class="myButton">Go</Link>
            <Link to="/searchbyvoice" class="myButton">Voice Search</Link>
              </div>
          </div>
        )  
    }
}

export default Home