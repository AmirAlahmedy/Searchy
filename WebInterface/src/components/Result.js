import React from 'react'
import axios from 'axios'
import {useState, useEffect} from 'react'
import '../Result.css'
import Results from './Results'
import Pagination from './Pagination'
const Result =()=>{
    const [posts,setPosts] = useState([]);
    const [loading,setLoading] = useState(false);
    const[currentPage,setCurrentPage]=useState(1)
    const [postsPerPage] = useState(10);
    useEffect(() => {
        const fetchPosts = async () =>{
            setLoading(true);
            const res = await axios.get('http://localhost:4000/results');
            setPosts(res.data);
            setLoading(false);
        };
        fetchPosts();
    }, []);
    // Get current posts
    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);

    // Change page
    const paginate = pageNumber => setCurrentPage(pageNumber);
    // const{posts}=this.state
    // const postList =posts.length ? (
    //     posts.map(post=>{
    //     return (
    //         <div className="post-card" key={post.id}>
    //             <div className="card-content">
    //              <a href="/"className="card-title red-text" >{post.title}</a>
    //                 <p>{post.body}</p>
    //             </div>
    //         </div>
    //     )
    //     })
    // ):
    //  (<div className="result-content">Searching results...</div>)
    return(
        <div className="fixed-top ">
            <h4 className="text mb-3">Search Results</h4>
            <Results posts={currentPosts} loading={loading} />
            <Pagination
                postsPerPage={postsPerPage}
                totalPosts={posts.length}
                paginate={paginate}
            />
        </div>

    )
}
export default Result