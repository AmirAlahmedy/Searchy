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
        displayLegend: false,
        legendPosition: 'right',
        location: 'Country'
    }
    

    render() {
        console.log((JSON.stringify(this.props.chartData) + "Mostafa"))
        return (
            <div className="chart">

                <Pie
                    data={this.props.chartData}
                    options={{
                        title: {
                            display: this.props.displayTitle,
                            text: 'Trends In ' + this.props.location,
                            fontSize: 25
                        },
                        // legend: {
                        //     display: this.props.displayLegend,
                        //     position: this.props.legendPosition
                        // }
                    }}
                />

                <Bar
            
                    data={this.props.chartData}
                    width={100}
                    height={48}
                    options={{
                        // responsive: false,
                        //  maintainAspectRatio: false,
                        title: {
                            display: this.props.displayTitle,
                            text: 'Trends In ' + this.props.location,
                            fontSize: 25
                        },
                        legend: {
                            display: this.props.displayLegend,
                            position: this.props.legendPosition
                        },
                        scales:{
                            yAxes:[{
                                display:true,
                                ticks:{
                                    beginAtZero:true,
                                    stepSize:1
                                }
                            }]
                        }
                    }}
                />

                

            </div>
        )
    }
}

export default Chart;