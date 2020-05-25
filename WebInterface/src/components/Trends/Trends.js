import React from 'react'
import axios from 'axios'
import {useState, useEffect} from 'react'
import Results from '../Result/Results'
import Pagination from '../Result/Pagination'
const Trends =()=>{
    const [posts,setPosts] = useState([]);
    const [loading,setLoading] = useState(false);
    const[currentPage,setCurrentPage]=useState(1)
    const [postsPerPage] = useState(10);
    useEffect(() => {
        const fetchPosts = async () =>{
            setLoading(true);
            const res = await axios.get('https://jsonplaceholder.typicode.com/posts');
            setPosts(res.data);
            setLoading(false);
        };
        fetchPosts();
    }, []);
  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);

  const paginate = pageNumber => setCurrentPage(pageNumber);
         return(
             <div>
            <div className="fixed-top ">
                <h4 className="text mb-3">Search Results</h4>
                <Results posts={currentPosts} loading={loading} />
      <Pagination
        postsPerPage={postsPerPage}
        totalPosts={posts.length}
        paginate={paginate}
      />
            </div>
            </div>
            
        )
    }
export default Trends