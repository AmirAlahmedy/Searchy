import React from 'react';

const Results = ({ posts, loading }) => {
  if (loading) {
    return <h2>Loading...</h2>;
  }

  return (
    <ul className='list-group mb-4'>
      {posts.map(post => (
        <div className="post-card" key={post.id}>
        <div className="card-content"  style={{marginLeft: '5%'}}>
          <a href="/"className="card-title red-text" >{post.title}</a>
          <p>{post.body}</p>
        </div>
        </div>
      ))}
    </ul>
  );
};

export default Results;