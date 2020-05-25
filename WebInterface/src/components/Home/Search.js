import React, {Component, useState} from 'react'
import {Link} from 'react-router-dom';
import './Search.css'
import Silogo from '../../silogo.png'
import Result from "../Result/Result";
import Route from "react-router-dom/es/Route";
import {Button} from 'react-bootstrap'
//import lib from 'react-speech-recognition';
//import Mic from './Mic'
import countries from "../../countries";
import Mic from "../Mic/Mic";
import Voice from "./mic-fill.svg";



class Search extends Component {
  constructor(props) {
    super(props);
    this.state = {
      query: '',
      suggestions: []
    }
  }

  handleChange = (e, v) => {
    const {items} = this.props;
    let query;
    if(e) query = e.target.value;
    else query = v;
    let suggestions = [];
    if (query.length > 0) {
      const regex = new RegExp(`^${query}`, `i`);
      suggestions = items.sort().filter(v => regex.test(v));
    }
    this.setState(() => ({suggestions, query: query}));
  }
  suggestionSelected = (query) => {
    this.setState(() => ({query: query, suggestions: []}));
  }

  renderSuggestions() {
    const {suggestions} = this.state
    if (suggestions.length === 0) {
      return null
    }
    return (
        <ul>
          {suggestions.map(item => {
            return (<li onClick={() => this.suggestionSelected(item)}>{item}</li>)
          })}
        </ul>
    )
  }


  render() {
    let {query} = this.state;
    const country = this.props.country;
    let trnscrpt = "";
    console.log(country);
    console.log(query);


      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      const SpeechGrammarList = window.SpeechGrammarList || window.webkitSpeechGrammarList;

      const grammar = '#JSGF V1.0;';

      const recognition = new SpeechRecognition();
      const speechRecognitionList = new SpeechGrammarList();
      speechRecognitionList.addFromString(grammar, 1);
      recognition.grammars = speechRecognitionList;
      recognition.lang = 'en-US';
      recognition.interimResults = false;

    recognition.onerror = (event) =>{
      alert("Erorr! "+event.error);
    }

    recognition.onspeechend = () => {
      recognition.stop();
    };

    recognition.onresult = (event) => {
      const last = event.results.length - 1;
      const command = event.results[last][0].transcript;
      console.log(command);
      query = command;
      document.getElementById("search-input").value = query;
      this.handleChange(null, command);
    };



    return (
        <div className="container" >
          <div className="AutoCompleteText">
            <input onChange={this.handleChange} value={query} type="text"
                   className="form-control form-control-sm ml-3 w-100" id="search-input" placeholder="Search..."/>
            <div>{this.renderSuggestions()}</div>
          </div>

          <div className="center flex-container" style={{
              display: "flex",
              flexWrap: "wrap",
            justifyContent: "space-between",
            alignItems: "center"
          }}>
              <div className={"buttons"}>
                <Link to={{
                  pathname: "/results/1",
                  state: {
                    searchQuery: query,
                    country: country
                  }
                   }}><Button className="myButton">Go</Button></Link>
                <Route path="/results/1"  render={ props => (
                    <Result {...props}/>
                )} />
                  <Link to="/trends"><Button className="myButton">Trends</Button></Link>
                  <Link to="/images"><Button className="myButton">Image Search</Button></Link>
              </div>

              <img src={Voice} id="voiceRecognition" className="img-responsive center-block"
                 alt="Voice Recognition"  onClick={ event => {recognition.start(); }}
                 style={{
                   minHeight: "20px",
                   minWidth: "20px",
                   height: "10%",
                   width: "10%",
                   marginTop: "5%",
                   position: "relative",
                   alignSelf: "center",
                   cursor: "pointer"
                 }}/>

          </div>
          {/*<div className="form-check" style={{*/}
          {/*  margin:"5% 5% 0 0"*/}
          {/*}}>*/}
          {/*  <input type="checkbox" className="form-check-input" id="exampleCheck1"/>*/}
          {/*    <label className="form-check-label" htmlFor="exampleCheck1">Image Search</label>*/}
          {/*</div>*/}
        </div>
    )
  }
}

export default Search