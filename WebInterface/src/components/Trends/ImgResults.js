import React from 'react';
import Silogo from '../../silogo.png'
const ImgResults = ({ posts, loading }) => {
  if (loading) {
    return <h2>Loading...</h2>;
  }

  return (
    <ul className='list-group mb-4'>
      {posts.map(post => (
        <div className="post-card" key={post.id}>
        <img src={Silogo} alt="my logo"/>
        <div className="card-content">
          <a href="/"className="card-title red-text" >{post.title}</a>
                         <p>{post.body}</p>
                     </div>
                 </div>
      ))}
    </ul>
  );
};

export default ImgResults;