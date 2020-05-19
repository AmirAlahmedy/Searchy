import React, {Component } from 'react'
import SpeechRecognition from 'react-speech-recognition'
import './Mic.css'

class Mic extends Component {
  render() {
    const { transcript, resetTranscript, browserSupportsSpeechRecognition } = this.props

    if (!browserSupportsSpeechRecognition) {
      return null
    }

    return (
      <div className="container">
        <button class="myButton"onClick={resetTranscript}>Reset</button>
    <div className="contentmic"><span>{transcript}</span></div>      
      </div>
    )
  }
}

export default SpeechRecognition(Mic)