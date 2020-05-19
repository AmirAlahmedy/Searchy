import React from 'react'
import axios from 'axios'
import {useState, useEffect} from 'react'
import Results from './Results'
import Pagination from './Pagination'
import { makeStyles } from '@material-ui/core/styles';
// import Pagination from '@material-ui/lab/Pagination';
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
    return(
        <div className="fixed-top ">
            <h4 className="text mb-3"  style={{marginLeft: '2%', marginTop: '1%', marginBottom: '1%'}}>Search Results</h4>
            <Results posts={currentPosts} loading={loading} />
            {/*<Pagination count={posts.length} color="secondary" />*/}
            <Pagination style={{marginBottom: '2%'}}
                postsPerPage={postsPerPage}
                totalPosts={posts.length}
                paginate={paginate}
                currentPage={currentPage}
            />
        </div>

    )
}
export default Result