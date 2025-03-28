package com.friendsbook.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "followingId", "followedByUserId" }))
public class Followings {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "followingId", referencedColumnName = "userId")
	@JsonBackReference
	private Users following;

	@Override
	public String toString() {
		return "Followings [id=" + id + ", following=" + following + ", followedByUser=" + followedByUser + "]";
	}

	@ManyToOne
	@JoinColumn(name = "followedByUserId", referencedColumnName = "userId")
	@JsonBackReference
	private Users followedByUser;
}
