import React, {useEffect, useState} from 'react'
import axios from 'axios'
import Results from '../Result/Results'
import Pagination from '../Result/Pagination'
import '../Result/Result.css'

const Trends = () => {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(1)
    const [postsPerPage] = useState(10);
    useEffect(() => {
        const fetchPosts = async () => {
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

    if(document.body.getElementsByTagName("canvas")[0])
    document.body.getElementsByTagName("canvas")[0].style.display = "none";

    return (
        <div>
            <h4 className="text mb-3" style={{marginLeft: '2%', marginTop: '1%', marginBottom: '1%'}}>Search
                Results</h4>
            {/*<svg height="100" width="100" className={'element'}>*/}
            {/*    <circle cx="50" cy="50" r="40" stroke="black" strokeWidth="3" fill="crimson"/>*/}
            {/*</svg>*/}
            <Results posts={currentPosts} loading={loading}/>
            <Pagination
                postsPerPage={postsPerPage}
                totalPosts={posts.length}
                paginate={paginate}
                currentPage={currentPage}
            />
        </div>


    )
}
export default Trends