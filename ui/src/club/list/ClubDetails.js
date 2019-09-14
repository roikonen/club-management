import React from 'react';
import ClubMemberTable from './ClubMemberTable';

import './ClubDetails.css';

class ClubDetails extends React.Component {
  render() {
    const club = this.props.club;

    return (
      <div className="ClubDetails">
        <div className="ClubName">{club.name}</div>
        <ClubMemberTable clubMembers={club.members} />
      </div>
    );
  }
}

export default ClubDetails;
