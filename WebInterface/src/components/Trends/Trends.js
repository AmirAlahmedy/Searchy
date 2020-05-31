import React, {useEffect, useState} from 'react'
import axios from '../../axios-instance'
import '../Result/Result.css'
import Chart from "./Chart";

const Trends = (props) => {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
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
        Trends: true,
        Images: false
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

    let name = [], frequency = [], bgColor = [], hbgColor = [];
    for (let post of posts) {
        name.push(post.name);
        frequency.push(post.frequency);

    }


    return (
        <div>
            <Chart {...props} chartData={{
                labels: name,
                datasets: [{
                    label: name,
                    backgroundColor: [
                        '#FF6384',
                        '#36A2EB',
                        '#FFCE56',
                        '#1BCD9A',
                        'rgba(255,105,145,0.6)',
                        'rgba(155,100,210,0.6)',
                        'rgba(90,178,255,0.6)',
                        'rgba(240,134,67,0.6)',
                        'rgba(120,120,120,0.6)',
                        'rgba(250,55,197,0.6)'
                    ],
                    borderWidth: 3,
                    data: frequency,
                }]
            }} location={country} legendPosition="bottom"/>
        </div>
    )
}
export default Trends