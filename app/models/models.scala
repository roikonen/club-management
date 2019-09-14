package models

/**
 * Model representing a club.
 *
 * @param name Club name
 * @param members Sequence of club members
 */
case class Club(name: String, members: Seq[Member] = Seq.empty)

/**
 * Model representing a club member.
 *
 * @param name Name of the club member
 */
case class Member(name: String)
