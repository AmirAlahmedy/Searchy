import React,{Component} from 'react'
import axios from 'axios'
import '../Result.css'
class Result extends Component{
    state={
        posts:[ ]
    }
    componentDidMount(){
        axios.get('http://localhost:4000/results')
        .then(res =>{
            console.log(res)
            this.setState({
                posts:res.data.slice(0,20)
            })
        })
    }
    render(){
        const{posts}=this.state
        const postList =posts.length ? (
            posts.map(post=>{
            return (
                <div className="post-card" key={post.id}>
                    <div className="card-content">
                     <a href="/"className="card-title red-text" >{post.title}</a>
                        <p>{post.body}</p>
                    </div>
                </div>
            )    
            })
        ):
         (<div className="result-content">Searching results...</div>)
        return(
            <div className="container">
                <h4 className="result-content">Results</h4>
                {postList}
            </div>
        )
    }
   
}
export default Result