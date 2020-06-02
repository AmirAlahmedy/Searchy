import React, {Component} from 'react'
import {Link} from 'react-router-dom';
import './Search.css'
import Result from "../Result/Result";
import Route from "react-router-dom/es/Route";
import {Button} from 'react-bootstrap'
import Voice from "./mic-fill.svg";
import axios from '../../axios-instance';

class Search extends Component {
    constructor(props) {
        super(props);
        this.state = {
            query: '',
            suggestions: []
        }
    }

    handleChange = async (e) => {
        let query = e.target.value;
        const response = await axios(`/results?query=${query}`);
        const suggestions = await response.data;

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
                {
                    suggestions.map(item => <li onClick={() => this.suggestionSelected(item)}>{item}</li>)
                }
            </ul>
        )
    }


    render() {
        let {query} = this.state?.query;
        const country = this.props.country;

        console.log(country);
        console.log(query);

        let recognition = null;
        if (window.chrome) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            const SpeechGrammarList = window.SpeechGrammarList || window.webkitSpeechGrammarList;

            const grammar = '#JSGF V1.0;';

            recognition = new SpeechRecognition();
            const speechRecognitionList = new SpeechGrammarList();
            speechRecognitionList.addFromString(grammar, 1);
            recognition.grammars = speechRecognitionList;
            recognition.lang = 'en-US';
            recognition.interimResults = false;

            recognition.onerror = (event) => {
                alert("Erorr! " + event.error);
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

        }

        const handleVoiceRecognition = (recognition) => {
            if (recognition) {
                recognition.start();
            } else {
                alert("Voice Recognition is supported on Chrome only");
            }
        }

        return (
            <div className="container">
                <form onSubmit={e => {e.preventDefault();}}>
                <div className="AutoCompleteText">
                    <input onChange={this.handleChange} value={query} type="text"
                           className="form-control form-control-sm ml-3 w-100" id="search-input"
                           placeholder="Search..."/>
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
                            pathname: "/results",
                            state: {
                                searchQuery: query,
                                country: country
                            }
                        }} id={"go"} ><Button type={"submit"} className="myButton">Go</Button></Link>
                        <Route path="/results" render={props => (
                            <Result {...props}/>
                        )}/>
                        <Link to={{
                            pathname: "/trends",
                            state: {
                                searchQuery: query,
                                country: country
                            }
                        }}><Button className="myButton">Trends</Button></Link>
                        <Link to={{
                            pathname: "/images",
                            state: {
                                searchQuery: query,
                                country: country
                            }
                        }}><Button className="myButton">Image Search</Button></Link>
                    </div>

                    <img src={Voice} id="voiceRecognition" className="img-responsive center-block"
                         alt="Voice Recognition" onClick={event => {
                        handleVoiceRecognition(recognition);
                    }}
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
                </form>
            </div>
        )

    }
}

export default Search