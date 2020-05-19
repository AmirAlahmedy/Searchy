import React from 'react';


const Pagination = ({ postsPerPage, totalPosts, paginate, currentPage }) => {
  const pageNumbers = [];

  for (let i = 1; i <= Math.ceil(totalPosts / postsPerPage); i++) {
    pageNumbers.push(i);
  }
  return (

    <nav style={{marginLeft: '5%'}}>
      <ul className='pagination' style={{margin: '0'}}>
          <li className="page-item">
              <a className="page-link" onClick={() => paginate(currentPage - 1)} href="!#" aria-label="Previous">
                  <span aria-hidden="true">&laquo;</span>
              </a>
          </li>
        {pageNumbers.map(number => (
          <li key={number} className='page-item'>
            <a onClick={() => paginate(number)} href="/results/!#"  className='page-link'>
              {number}
            </a>
          </li>
        ))}
          <li className="page-item">
              <a className="page-link" onClick={() => paginate(currentPage + 1)} href="!#" aria-label="Next">
                  <span aria-hidden="true">&raquo;</span>
              </a>
          </li>
      </ul>
    </nav>
  );
};

export default Pagination;