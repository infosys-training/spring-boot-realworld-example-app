import React from "react";

const ListErrors = ({ errors }) => {
  if (!errors) return null;
  
  const errorMessages = [];
  
  if (Array.isArray(errors)) {
    errors.forEach((error, index) => {
      errorMessages.push(<li key={index}>{error}</li>);
    });
  } else if (typeof errors === 'object') {
    Object.keys(errors).forEach((key) => {
      errorMessages.push(
        <li key={key}>
          {key} {errors[key]}
        </li>
      );
    });
  }
  
  if (errorMessages.length === 0) return null;
  
  return <ul className="error-messages">{errorMessages}</ul>;
};

export default ListErrors;
