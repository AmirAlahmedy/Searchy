import React from 'react'
import {useState, useEffect} from 'react'
import Results from './Results'
import Pagination from './Pagination'
import  axios  from '../../axios-instance'
import './Result.css'


const Result = props =>{
    const [posts,setPosts] = useState([]);
    const [loading,setLoading] = useState(false);
    const[currentPage,setCurrentPage]=useState(1)
    const [postsPerPage] = useState(10);

    if(props.location.state) {
        localStorage.setItem('searchQuery',  props.location.state.searchQuery);
        localStorage.setItem('country',  props.location.state.country);
    }
    const searchQuery = localStorage.getItem('searchQuery');
    const country = localStorage.getItem('country');

    console.log(searchQuery, country);

    let data = {
        searchQuery1: searchQuery,
        Country: country
    }

    console.log(data);
    let _data = JSON.stringify(data);


    useEffect(() => {
        const fetchPosts =  () =>{

            setLoading(true);

            axios.post('/results', _data).then(r => {
                    console.log(r);
                    setPosts(r.data);
                    setLoading(false);
                }).catch(error => {
                    console.error(error);
                });
        };
        fetchPosts();
    }, []);

    // Get current posts
    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);
    window.history.pushState(null, null, '/results/'+currentPage);
    console.log(currentPosts.length);
    const paginate = pageNumber => setCurrentPage(pageNumber);
    return(

        <div>
            <h4 className="text mb-3"  style={{marginLeft: '2%', marginTop: '1%', marginBottom: '1%'}}>Search Results</h4>
            <Results posts={currentPosts} loading={loading} />
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