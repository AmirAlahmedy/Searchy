import React, {Component, useState} from "react";
import Search from "./Search";
import App from "../../App";
import countries from "../../countries";
import Silogo from "../../silogo.png";
import CountryDropdown from "./Dropdown";

class Home extends Component {

    state  = {
        country: ''
    }

    selectCountry (val) {
        this.setState({ country: val });
    }

    getCountryValue () {
        return this.state.country;
    }
    render() {
        return(
            <div>
            <img src={Silogo} alt="Searchy" style={{
                "align-self": "center",
                "vertical-align": "middle",
                "left": "50%",
                "right": "50%",
                "position": "absolute",
                "transform": "translate(-50%)"
            }}/>
            <Search   items={countries}  country={this.getCountryValue()}/>
            <CountryDropdown value={this.getCountryValue()} onChange={(val) => {this.selectCountry(val); console.log(this.getCountryValue()) }} style={{
                "left": "75%",
                "right": "25%",
                "position": "absolute",
                "top": "95%"
            }}/>

            </div>
        );
    }
}

export default Home;