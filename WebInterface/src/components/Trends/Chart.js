import React, {Component} from 'react';
import {Bar, Pie} from 'react-chartjs-2';

class Chart extends Component {
    constructor(props) {
        super(props);
        this.state = {
            chartData: props.chartData
        }
    }

    static defaultProps = {
        displayTitle: true,
        displayLegend: true,
        legendPosition: 'right',
        location: 'City'
    }

    render() {
        console.log((this.props.chartData))
        return (
            <div className="chart">

                <Bar
                    data={this.props.chartData}
                    width={100}
                    height={50}
                    options={{
                        maintainAspectRatio: false
                    }}
                />

                <Pie
                    data={this.props.chartData}
                    options={{
                        title: {
                            display: this.props.displayTitle,
                            text: 'Trends In ' + this.props.location,
                            fontSize: 25
                        },
                        legend: {
                            display: this.props.displayLegend,
                            position: this.props.legendPosition
                        }
                    }}
                />

            </div>
        )
    }
}

export default Chart;