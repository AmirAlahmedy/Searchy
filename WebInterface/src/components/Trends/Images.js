import React, {useEffect, useState} from 'react'
import axios from '../../axios-instance'
import ImgResults from './ImgResults'
import Pagination from '../Result/Pagination'
import '../Result/Result.css'

const Result = (props) => {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(1)
    const [postsPerPage] = useState(10);

    if (props.location.state) {
        localStorage.setItem('searchQuery', props.location.state.searchQuery);
        localStorage.setItem('country', props.location.state.country);
    }
    const searchQuery = localStorage.getItem('searchQuery');
    const country = localStorage.getItem('country');
    console.log(searchQuery, country);

    let data = {
        searchQuery: searchQuery,
        Country: country,
        Trends: false,
        Images: true
    }
    console.log(data);
    let _data = JSON.stringify(data);
    useEffect(() => {
        const fetchPosts = () => {

            setLoading(true);
            axios.post('/results', _data).then(r => {
                console.log(r);
                setPosts(r.data);
                setLoading(false);
            }).catch(error => {
                console.error(error);
            });
        };
        // const fetchPosts = async () => {
        //     setLoading(true);
        //     const res = await axios.get('https://jsonplaceholder.typicode.com/posts');
        //     setPosts(res.data);
        //     setLoading(false);
        // };
        fetchPosts();
    }, []);
    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);
    const paginate = pageNumber => setCurrentPage(pageNumber);

    document.body.style.overflow = "visible";
    if (document.body.getElementsByTagName("canvas")[0])
        document.body.getElementsByTagName("canvas")[0].style.display = "none";

    return (
        <div className="container" style={{
            top: "0"
        }}>
            <h4 className="center">Search Results</h4>
            <ImgResults posts={currentPosts} loading={loading}/>
            <Pagination style={{marginBottom: '2%', float: 'bottom'}}
                postsPerPage={postsPerPage}
                totalPosts={posts.length}
                paginate={paginate}
                currentPage={currentPage}
            />
        </div>
    )
}
export default Result