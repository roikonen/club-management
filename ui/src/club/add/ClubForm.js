import React from "react"

import Client from "../../Client";

import './ClubForm.css';

class ClubForm extends React.Component {

  static InitialState() {
    return {
      club: {
        name: "",
        members: [{ name: "" }]
      },
      result: ""
    }
  }

  state = ClubForm.InitialState();

  handleChange = (e) => {
    if (["name"].includes(e.target.className)) {
      let club = { ...this.state.club }
      club.members[e.target.dataset.id][e.target.className] = e.target.value
      this.setState({ club })
    } else {
      let club = { ...this.state.club }
      club.name = e.target.value
      this.setState({ club })
    }
  }

  addMember = (e) => {
    let club = { ...this.state.club }
    club.members = [...this.state.club.members, { "name": "" }]
    this.setState({ club });
  }

  removeMember = (id, e) => {
    let club = { ...this.state.club }
    club.members.splice(id, 1)
    this.setState({ club })
  }

  handleSubmit = (e) => {
    let result = { ...this.state.result }
    result = "Saving..."
    this.setState({ result })
    e.preventDefault()
    Client.postClub(this.state.club, response => {
      this.setState(ClubForm.InitialState())
      result = response.ok ? "Club successfully stored." : "Storing club failed."
      this.setState({ result })
    });
  }

  validate = (state) => {
    var valid = state.club.name;
    state.club.members.forEach(function (member) {
      if (!member.name) valid = false;
    });
    return valid;
  }

  render() {
    let { name, members } = this.state.club
    return (
      <div>
        <form className="ClubForm">
          {this.state.result ? <div>{this.state.result}</div> : null}
          <label htmlFor="name">Club Name</label>
          <input type="text" name="name" id="name" value={name} onChange={this.handleChange} />
          <button className="AddButton" type="button" onClick={this.addMember}>Add Member</button>
          {
            members.map((val, idx) => {
              let nameId = `name-${idx}`
              return (
                <div key={idx} className="ClubMemberRow">
                  <label htmlFor={nameId}>{`Member ${idx + 1} Name`}</label>
                  <input
                    type="text"
                    name={nameId}
                    data-id={idx}
                    id={nameId}
                    value={members[idx].name}
                    className="name"
                    onChange={this.handleChange}
                  />
                  <button className="RemoveButton" type="button" onClick={() => this.removeMember(idx)} disabled={members.length === 1}>Remove</button>
                </div>
              )
            })
          }
          <button className="SubmitButton" onClick={this.handleSubmit} type="submit" disabled={!this.validate(this.state)}>Create</button>
        </form>
      </div>
    )
  }
}

export default ClubForm
