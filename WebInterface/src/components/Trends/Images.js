import React, {useEffect, useState} from 'react'
import axios from 'axios'
import ImgResults from './ImgResults'
import Pagination from '../Result/Pagination'
// import Silogo from '../silogo.png' 
const Result = () => {
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

    document.body.style.overflow = "visible";
    if(document.body.getElementsByTagName("canvas")[0])
        document.body.getElementsByTagName("canvas")[0].style.display = "none";

    return (
        <div className="container">
            <h4 className="center">Search Results</h4>
            <ImgResults posts={currentPosts} loading={loading}/>
            <Pagination
                postsPerPage={postsPerPage}
                totalPosts={posts.length}
                paginate={paginate}
                currentPage={currentPage}
            />
        </div>
    )
}
export default Result