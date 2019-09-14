import React from 'react';
import ClubDetails from './ClubDetails';
import Client from "../../Client";

class ClubList extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      clubs: [],
      loaded: false
    };
  }

  componentDidMount() {
    Client.getClubs(clubs => {
      this.setState({
        clubs: clubs,
        loaded: true
      })
    });
  }

  render() {
    const rows = [];



    this.state.clubs.forEach((club) => {
      rows.push(
        <ClubDetails club={club} key={club.name} />
      );
    });

    return (
      <div className="ClubList">
        {this.state.loaded && this.state.clubs.length === 0 ? <div>No clubs yet.</div> : null}
        {rows}
      </div>
    );
  }
}

export default ClubList;
