import React from 'react';


const Pagination = ({ postsPerPage, totalPosts, paginate, currentPage }) => {
  const pageNumbers = [];

  for (let i = 1; i <= Math.ceil(totalPosts / postsPerPage); i++) {
    pageNumbers.push(i);
  }

  let pageNumbersElements = document.getElementsByClassName("pgnum");
  for(let item of pageNumbersElements) {
      console.log(item);
      if(item.value == currentPage) {
          item.style.color = 'tomato';
      } else {
          item.style.color = '#0056b3';
      }
  }


  return (

      <ul className='pagination' style={{margin: '0', marginLeft: '5%'}}>
          <li className="page-item">
              <button className="page-link" onClick={() => paginate(currentPage - 1)}  aria-label="Previous">
                  <span aria-hidden="true">&laquo;</span>
              </button>
          </li>
        {pageNumbers.map(number => (
          <li key={number} className='page-item'>
            <button onClick={() => paginate(number)}   value={number} className='page-link pgnum'>
              {number}
            </button>
          </li>
        ))}
          <li className="page-item">
              <button className="page-link" onClick={() => paginate(currentPage + 1)}  aria-label="Next">
                  <span aria-hidden="true">&raquo;</span>
              </button>
          </li>
      </ul>

  );
};

export default Pagination;