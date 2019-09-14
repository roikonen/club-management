import React from 'react';
import {
  BrowserRouter as Router,
  Route,
  Link
} from 'react-router-dom';

import ClubForm from './club/add/ClubForm';
import ClubList from './club/list/ClubList';

import './App.css';

class App extends React.Component {

  render() {
    return (
      <Router>
        <div className="App">
          <h1>Club Management App</h1>
          <nav className="nav">
            <Link to="add">Add Club</Link>
            <Link to="list">List Clubs</Link>
          </nav>
          <Route path="/add" component={() => <ClubForm />}/>
          <Route path="/list" component={() => <ClubList />}/>
        </div>
      </Router>
    );
  }
}

export default App;
