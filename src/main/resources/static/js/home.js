document.getElementById('profileeButton').addEventListener('click', function() {
	const userId = localStorage.getItem('userId');
	if (userId) {
		window.location.href = `/friendsbook/images/profile?userId=${userId}`;
	} else {
		alert('User ID not found');
	}
});
window.onload = function() {

	const userId = localStorage.getItem('userId');
	fetchPostsFromFollowings(userId);
	getNotifications();
};
function getAllUsers() {
	fetch('/friendsbook/users')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			console.log(response);
			return response.json();
		})
		.then(data => {
			const usersListElement = document.getElementById('users-list');
			const allUsersContainer = document.getElementById('all-users');
			usersListElement.innerHTML = '';

			if (data.length > 0) {
				data.forEach(user => {
					const listItem = document.createElement('li');
					listItem.textContent = `ID: ${user.userId}, Email: ${user.userEmail}`;
					usersListElement.appendChild(listItem);
				});
				allUsersContainer.style.display = 'block';
			} else {
				allUsersContainer.style.display = 'none';
			}
		})
		.catch(error => {
			console.error("Error fetching users:", error);
			alert("Failed to load users.");
		});
}


function showLoader() {
	const loader = document.getElementById('loader');
	if (loader) {
		loader.style.display = 'block';
	}
}

function hideLoader() {
	const loader = document.getElementById('loader');
	if (loader) {
		loader.style.display = 'none';
	}
}

async function fetchUserData() {
	const userId = document.getElementById("userId").value;


	if (!userId) {
		alert("Please enter a User ID.");
		return;
	}

	try {

		const response = await fetch(`/friendsbook/search?userId=${userId}`);

		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}

		const user = await response.json();
		console.log("Fetched user data:", user);


		if (user && user.userId) {
			document.getElementById("user-id").textContent = user.userId;
			document.getElementById("user-email").textContent = user.userEmail;


			document.getElementById("user-info").style.display = "block";
			document.getElementById("error-message").style.display = "none";


			const loggedInUserId = localStorage.getItem('userId');


			checkIfFollowings(loggedInUserId, user.userId);
			checkFollowRequests(loggedInUserId, user.userId);
		} else {
			throw new Error("User data is incomplete.");
		}
	} catch (error) {
		console.error("Error fetching user:", error);


		document.getElementById("user-info").style.display = "none";
		document.getElementById("error-message").style.display = "block";
	}
}

function createButton(text, onClick) {
	const button = document.createElement('button');
	button.textContent = text;
	button.className = 'follow-btn';
	button.onclick = onClick;
	return button;
}

function checkIfFollowings(followerId, followedUserId) {
	fetch(`/friendsbook/following/exists?followerId=${followerId}&followedUserId=${followedUserId}`)
		.then(response => response.json())
		.then(isFollowing => {
			const followButtonContainer = document.getElementById('follow-button');

			followButtonContainer.innerHTML = '';

			if (isFollowing) {
				const unfollowButton = createButton('Unfollow', () => unfollowUser(followerId, followedUserId));
				followButtonContainer.appendChild(unfollowButton);
			} else {
				checkFollowRequests(followerId, followedUserId);
			}
		})
		.catch(error => console.error("Error checking follow status:", error));
}
function checkFollowRequests(followerId, followedUserId) {
	fetch(`/friendsbook/notifications/check/follow?senderId=${followerId}&recipientId=${followedUserId}`)
		.then(response => response.json())
		.then(isRequested => {
			const followButtonContainer = document.getElementById('follow-button');

			followButtonContainer.innerHTML = '';

			if (isRequested) {

				const requestedButton = createButton('Requested', () => { });
				requestedButton.addEventListener('dblclick', () => {
					cancelFriendRequest(followerId, followedUserId);
				});
				followButtonContainer.appendChild(requestedButton);
			} else {

				const followButton = createButton('Follow', () => sendFriendRequest(followerId, followedUserId));
				followButtonContainer.appendChild(followButton);
			}
		})
		.catch(error => console.error("Error checking follow request:", error));
}

function cancelFriendRequest(senderId, recipientId) {

	console.log("inside cancle friend request");
	const url = `/friendsbook/notifications/friend-requests/${senderId}/${recipientId}`;


	fetch(url, {
		method: 'DELETE',
		headers: {
			'Content-Type': 'application/json',
		}
	})
		.then(response => {

			if (!response.ok) {
				return Promise.reject('Failed to cancel friend request. Server returned: ' + response.statusText);
			}


			return response.text();
		})
		.then(data => {

			alert(data);
		})
		.catch(error => {

			console.error('Error:', error);
			alert('There was an error while canceling the friend request: ' + error);
		});
}


function createButton(text, onClick) {
	const button = document.createElement('button');
	button.textContent = text;
	button.onclick = onClick;
	return button;
}


function sendFriendRequestt(followerId, followedUserId) {
	fetch(`/friendsbook/notifications/sendFriendRequest?senderId=${followerId}&recipientId=${followedUserId}`, {
		method: 'POST',
	})
		.then(response => response.json())
		.then(result => {
			console.log("Friend request sent:", result);
			fetchUserData();
		})
		.catch(error => console.error('Error sending friend request:', error));
}

function unfollowUser(followerId, followedUserId) {
	fetch(`/friendsbook/${followerId}/unfollow/${followedUserId}`, {
		method: 'DELETE'
	})
		.then(response => {
			if (response.ok) {
				alert("Successfully unfollowed");
				console.log('Successfully unfollowed the user');
			} else {
				return response.json().then(error => {
					console.error('Error unfollowing user:', error);
				});
			}
		})
		.catch(error => {
			console.error('Error:', error);
		});
}


async function sendFriendRequest() {
	const senderId = localStorage.getItem('userId');
	const recipientId = document.getElementById("user-id").textContent;

	if (!senderId || !recipientId) {
		alert("Please ensure both sender and recipient IDs are available.");
		return;
	}

	try {
		const response = await fetch(`/friendsbook/notifications/sendFriendRequest?senderId=${senderId}&recipientId=${recipientId}`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
		});

		const result = await response.text();

		if (response.ok) {

			const followButton = document.getElementById("follow-button");
			followButton.textContent = "Request Sent";
			followButton.disabled = true;
			alert(result);
		} else {
			alert(result);
		}
	} catch (error) {
		console.error("Error sending friend request:", error);
		alert("Error sending friend request.");
	}
}

window.onload = function() {
	const userId = localStorage.getItem('userId');
	const status = 'pending';
	const notificationCountDiv = document.getElementById('notification-count');
	if (!notificationCountDiv) {
		console.error('Error: Notifications elements not found.');
		return;
	}
	fetch(`/friendsbook/notifications/user/${userId}/status/${status}`)
		.then(response => response.json())
		.then(data => {
			notificationCountDiv.innerHTML = '';
			if (data.length > 0) {
				notificationCountDiv.innerHTML = `${data.length}`;
			} else {
				notificationCountDiv.innerHTML = '0';
			}
		})
		.catch(error => {
			console.error('Error fetching notifications:', error);
		});
};

function getNotifications() {
	const userId = localStorage.getItem('userId');
	const status = 'pending';

	const notificationsListDiv = document.getElementById('notifications-list');

	if (!notificationsListDiv) {
		console.error('Error: Notifications list elements not found.');
		return;
	}

	fetch(`/friendsbook/notifications/user/${userId}/status/${status}`)
		.then(response => response.json())
		.then(data => {
			notificationsListDiv.innerHTML = '';

			if (data.length > 0) {
				data.forEach(notification => {
					console.log(notification);
					let message = removeFirstWord(notification.message);
					const notificationDiv = document.createElement('div');
					notificationDiv.classList.add('notification');

					const sender = notification.message.split(" ")[0];

					if (message === "sent you a friend request.") {
						checkIfFollowing(userId, sender, (isFollowing) => {
							if (isFollowing) {

								notificationDiv.innerHTML = `
	                                       <p>${notification.message}</p>
	                                       <button onclick="updateStatus(${notification.id}, 'accepted')">Accept</button>
	                                       <button onclick="updateStatus(${notification.id}, 'declined')">Decline</button>
	                                   `;
							} else {

								notificationDiv.innerHTML = `
	                                       <p>${notification.message}</p>
	                                       <button onclick="updateStatus(${notification.id}, 'accepted')">Accept</button>
	                                       <button onclick="updateStatus(${notification.id}, 'declined')">Decline</button>
	                                       <button id="follow-btn" onclick="sendFriendRequestToUser('${userId}', '${sender}')">Follow</button>
	                                   `;
							}
						});
					} else {
						notificationDiv.innerHTML = `
	                               <p>${notification.message}</p>
	                               <button onclick="updateStatus(${notification.id}, 'accepted')">Ok</button>
	                           `;
					}

					notificationsListDiv.appendChild(notificationDiv);
				});
			} else {
				notificationsListDiv.innerHTML = '<p>No pending notifications.</p>';
			}
		})
		.catch(error => {
			console.error('Error fetching notifications:', error);
		});
}

function checkIfFollowing(followerId, followedUserId, callback) {
	fetch(`/friendsbook/following/exists?followerId=${followerId}&followedUserId=${followedUserId}`)
		.then(response => response.json())
		.then(isFollowing => {
			callback(isFollowing);
		})
		.catch(error => console.error("Error checking follow status:", error));
}


async function sendFriendRequestToUser(userId, followerId) {
	const senderId = userId;
	const recipientId = followerId;

	if (!senderId || !recipientId) {
		alert("Please ensure both sender and recipient IDs are available.");
		return;
	}

	try {
		const response = await fetch(`/friendsbook/notifications/sendFriendRequest?senderId=${senderId}&recipientId=${recipientId}`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
		});

		const result = await response.text();

		if (response.ok) {
			const followButton = document.getElementById("follow-btn");


			if (!followButton) {
				console.error("Button with id 'follow-btn' not found");
				return;
			}


			followButton.textContent = "Request Sent";
			console.log("Button text set to 'Request Sent'");

			followButton.addEventListener("click", () => {
				console.log("Double-click detected, attempting to cancel friend request");
				cancelFriendRequest(senderId, recipientId);
			});

			alert(result);
		} else {
			alert(result);
		}
	} catch (error) {
		console.error("Error sending friend request:", error);
		alert("Error sending friend request.");
	}
}



function removeFirstWord(str) {

	let firstSpaceIndex = str.indexOf(' ');


	if (firstSpaceIndex === -1) {
		return "";
	}


	return str.substring(firstSpaceIndex + 1);
}

function updateStatus(notificationId, status) {
	fetch(`/friendsbook/notifications/update-status/${notificationId}?status=${status}`, {
		method: 'POST',
	})
		.then(response => {
			if (status === 'accepted') {
				console.log('Notification accepted!');
			} else if (status === 'declined') {
				console.log('Notification declined!');
			}
			getNotifications();
		})
		.catch(error => {
			console.error('Error updating notification status:', error);
		});
}

function fetchPostsFromFollowings(userId) {
	console.log("fetch posts from followings ");
	fetch(`/friendsbook/followings/posts/${userId}`)
		.then(response => {
			if (!response.ok) {
				throw new Error("Failed to fetch posts");
			}
			return response.json();
		})
		.then(posts => {
			const postsContainer = document.getElementById('followingpostsContainer');

			console.log("length of posts is : ");
			console.log(posts.length);
			if (posts && posts.length > 0) {
				posts.forEach(post => {
					const postDiv = document.createElement('div');

					const userName = document.createElement('p');
					userName.innerHTML = `<strong>${post.userId}</strong>`;
					postDiv.appendChild(userName);


					if (post.image) {
						const img = document.createElement('img');
						img.src = `data:image/jpeg;base64,${post.image}`;
						img.alt = 'Post Image';
						postDiv.appendChild(img);
					}

					const caption = document.createElement('p');
					caption.textContent = `${post.userId} : ${post.caption}`;
					postDiv.appendChild(caption);

					const likeButton = document.createElement('button');
					likeButton.textContent = `Like ${post.likeCount}`;
					likeButton.onclick = function() {
						likePost(post.postId, userId);
					};
					postDiv.appendChild(likeButton);

					postsContainer.appendChild(postDiv);



					const commentSection = document.createElement('div');
					const commentInput = document.createElement('input');
					commentInput.type = 'text';
					commentInput.placeholder = 'Add a comment...';
					commentInput.id = `comment-input-${post.postId}`;

					const commentButton = document.createElement('button');
					commentButton.textContent = 'Add Comment';
					commentButton.onclick = function() {
						addComment(post.postId);
					};

					const commentsList = document.createElement('div');
					commentsList.id = `comments-list-${post.postId}`;

					commentSection.appendChild(commentInput);
					commentSection.appendChild(commentButton);
					commentSection.appendChild(commentsList);
					postDiv.appendChild(commentSection);


					fetchComments(post.postId, userId);

				});
			}
		})
		.catch(error => {
			console.error("Error fetching posts:", error);
		});
}
