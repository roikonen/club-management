import React from 'react';
import ClubMemberRow from './ClubMemberRow';

import './ClubMemberTable.css';

class ClubMemberTable extends React.Component {
  render() {
    const rows = [];

    this.props.clubMembers.forEach((clubMember) => {
      rows.push(
        <ClubMemberRow clubMember={clubMember} key={clubMember.name}  />
      );
    });

    return (
      <table className="ClubMemberTable">
        <thead>
          <tr>
            <th>Members</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </table>
    );
  }
}

export default ClubMemberTable;
