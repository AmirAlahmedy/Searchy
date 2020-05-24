import React, { Component } from 'react';
import PropTypes from 'prop-types';
import countries from "../../countries";

export default class CountryDropdown extends Component {

    constructor (props) {
        super(props);

        this.state = {
            countries: countries
        };
    }

    getCountries () {

        return this.state.countries.map((countryName) => (
            <option value={countryName} key={countryName}>
                {countryName}
            </option>
        ));
    }

    getDefaultOption () {
        const { showDefaultOption, defaultOptionLabel } = this.props;
        if (!showDefaultOption) {
            return null;
        }
        return (
            <option value="" key="default">{defaultOptionLabel}</option>
        );
    }

    render () {
        // unused properties deliberately added so arbitraryProps gets populated with anything else the user specifies
        const { name, id, classes, value, onChange, onBlur, disabled, showDefaultOption, defaultOptionLabel,
            labelType, valueType, whitelist, blacklist, customOptions, priorityOptions, ...arbitraryProps } = this.props;

        const attrs = {
            ...arbitraryProps,
            name,
            value,
            onChange: (e) => onChange(e.target.value, e),
            onBlur: (e) => onBlur(e)
        };
        if (id) {
            attrs.id = id;
        }
        if (classes) {
            attrs.className = classes;
        }

        return (
            <select {...attrs}>
                {console.log()}
                {this.getDefaultOption()}
                {this.getCountries()}
            </select>
        );
    }
}

CountryDropdown.propTypes = {
    value: PropTypes.string,
    name: PropTypes.string,
    id: PropTypes.string,
    classes: PropTypes.string,
    showDefaultOption: PropTypes.bool,
    defaultOptionLabel: PropTypes.string,
    priorityOptions: PropTypes.array,
    onChange: PropTypes.func,
    onBlur: PropTypes.func,
    whitelist: PropTypes.array,
    blacklist: PropTypes.array,
    disabled: PropTypes.bool
};
CountryDropdown.defaultProps = {
    value: '',
    name: 'rcrs-country',
    id: '',
    classes: '',
    showDefaultOption: true,
    defaultOptionLabel: 'Select Country',
    priorityOptions: [],
    onChange: () => {},
    onBlur: () => {},
    whitelist: [],
    blacklist: [],
    disabled: false
};

