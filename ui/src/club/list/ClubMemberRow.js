import React from 'react';

class ClubMemberRow extends React.Component {
  render() {
    const clubMember = this.props.clubMember;
    const name = clubMember.name

    return (
      <tr>
        <td>{name}</td>
      </tr>
    );
  }
}

export default ClubMemberRow;
