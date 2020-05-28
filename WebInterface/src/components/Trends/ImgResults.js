import React from 'react';

const ImgResults = ({posts, loading}) => {
  if (loading) {
    return <h2>Loading...</h2>;
  }

  return (
      <ul className='list-group mb-4'>
        {posts.map(post => (
            <div className="column" key={post.id}>
              <img src={post.src} alt="my logo" style={{
                borderRadius: "0"
              }}/>
              <div className="card-content">
                <a href={post.url} className="card-title red-text" style={{
                  color: "green",
                  textDecoration: "underline",
                  fontSize: "8pt"
                }}>{post.url.slice(0, 60)}</a>
              </div>
            </div>
        ))}
      </ul>
  );
};

export default ImgResults;