import React, {Component, useState} from 'react'
import {Link} from 'react-router-dom';
import './Search.css'
import Silogo from '../../silogo.png'
import Result from "../Result/Result";
import Route from "react-router-dom/es/Route";
//import lib from 'react-speech-recognition';
//import Mic from './Mic'
import countries from "../../countries";


class Search extends Component {
  constructor(props) {
    super(props);
    this.state = {
      query: '',
      suggestions: []
    }
  }

  handleChange = (e) => {
    const {items} = this.props;
    const query = e.target.value;
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
    const {query} = this.state;
    const country = this.props.country;
    console.log(country);
    console.log(query);

    return (
        <div className="container">

          <div className="AutoCompleteText">
            <input onChange={this.handleChange} value={query} type="text"
                   class="form-control form-control-sm ml-3 w-100" id="search-input" placeholder="Search..."/>
            <div>{this.renderSuggestions()}</div>
          </div>
          <div className="center">
            <Link to={{
              pathname: "/results/1",
              state: {
                searchQuery: query,
                country: country
              }
               }} className="myButton">Go</Link>
            <Route path="/results/1"  render={ props => (
                <Result {...props}/>
            )} />
            <Link to="/searchbyvoice" className="myButton">Voice Search</Link>
          </div>
        </div>
    )
  }
}

export default Search