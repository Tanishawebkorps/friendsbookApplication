

document.getElementById('homeButton').addEventListener('click', function() {
	window.location.href = '/friendsbook/userhome';
});

function uploadImage() {
	console.log(userId);
	console.log("Uploading image...");

	const fileInput = document.getElementById('image-upload');
	const file = fileInput.files[0];

	if (!file) {
		alert("Please select an image!");
		return;
	}

	const formData = new FormData();
	formData.append("file", file);
	formData.append("userId", userId);

	fetch("/friendsbook/images/upload", {
		method: "POST",
		body: formData
	})
		.then(response => response.text())
		.then(imageUrl => {
			console.log('Image uploaded successfully:', imageUrl);

			
			const contextPath = '/friendsbook';
			const imageWithContextPath = contextPath + imageUrl;

			const profileImageElement = document.getElementById('profile-image');
			profileImageElement.src = imageWithContextPath;
			profileImageElement.style.display = 'block';  

			
			alert("Profile image uploaded successfully!");
		})
		.catch(error => {
			console.error("Error:", error);
			alert("Error uploading image.");
		});
}
function toggleDiv() {
	console.log("in toggleDiv");
	var div = document.getElementsByClassName('bio-container')[0];
	if (div.classList.contains('hidden')) {
		div.classList.remove('hidden');
		div.classList.add('visible');
	} else {
		div.classList.remove('visible');
		div.classList.add('hidden');
	}
}

function togglePostDiv() {
	var div = document.getElementsByClassName('upload-post-container')[0];
	if (div.classList.contains('hidden')) {
		div.classList.remove('hidden');
		div.classList.add('visible');
	} else {
		div.classList.remove('visible');
		div.classList.add('hidden');
	}
}
function updateBio() {
	console.log("inside bio update");
	var userBio = document.getElementById('bio-input').value;  
    var bio = document.getElementById('bio-area');
	if (!userBio) {
		alert("Please enter a bio!");
		return;
	}

	const url = `/friendsbook/updateBio?userEmail=${encodeURIComponent(userEmail)}&userBio=${encodeURIComponent(userBio)}`;


	fetch(url, {
		method: "POST",
	})
		.then(response => response.json())  
		.then(success => {
			const messageElement = document.getElementById('message');
			if (success) {
				bio.textContent=userBio;
				messageElement.textContent = "Bio updated successfully!";
				messageElement.style.color = "green";
				alert("Bio Updated successfully!");
			} else {
				messageElement.textContent = "Failed to update bio.";
				messageElement.style.color = "red";
			    alert("Failed to update bio.");
			}
		})
		.catch(error => {
			console.error("Error:", error);
			const messageElement = document.getElementById('message');
			messageElement.textContent = "An error occurred. Please try again.";
			messageElement.style.color = "red";
		});
}


function likePost(postId) {
    console.log("Inside likePost");
    const userId = localStorage.getItem('userId'); 

    $.post(`/friendsbook/like/${postId}/${userId}`, function(response) {
        if (response === "Post liked successfully!") {
           
            updateLikeCount(postId);
          
            sendLikeNotification(postId, userId);  
			alert("post liked successfully");
        }
		else {
            alert(response); 
        }
    });
}


function sendLikeNotification(postId, userId) {
    $.post(`/friendsbook/notifications/sendLikeNotification`, {
        postId: postId,
        senderId: userId
    }, function(response) {
        console.log('Notification sent:', response);
    }).fail(function(error) {
        console.error('Error sending notification:', error);
    });
}

function updateLikeCount(postId) {
    $.get(`/friendsbook/like-count/${postId}`, function(likeCount) {
        console.log("Like Count:", likeCount);
        $(`#like-count-${postId}`).text(`${likeCount} likes`);
    }).fail(function(error) {
        console.log("Error fetching like count:", error);
    });
	}


document.getElementById('postForm').addEventListener('submit', function (e) {
    e.preventDefault(); 
    const userId = localStorage.getItem('userId');
    const formData = new FormData();
    const fileInput = document.getElementById('file');
    const captionInput = document.getElementById('caption');

    formData.append('file', fileInput.files[0]);  
    formData.append('caption', captionInput.value); 

    fetch(`/friendsbook/posts/${userId}`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json()) 
    .then(postData => {
        if (postData && postData.postId) {
            const postId = postData.postId;  
            const contextPath = '/friendsbook'; 
            const imageUrl = postData.imageUrl; 

           
            const postContainer = document.createElement('div');
            postContainer.classList.add('post-item');  
            postContainer.style.marginBottom = '20px'; 

            
            const imgElement = document.createElement('img');
            imgElement.src = contextPath + imageUrl;  

            
            const captionElement = document.createElement('p');
            captionElement.textContent = captionInput.value || 'No caption'; 

            
            const likeButton = document.createElement('button');
            likeButton.textContent = 'Like';  
            likeButton.classList.add('like-button');
			
			
            const likeCountSpan = document.createElement('span');
            likeCountSpan.id = `like-count-${postId}`; 
            likeCountSpan.textContent = '';  

           
            likeButton.onclick = function () {
                likePost(postId);  
            };

          
            const commentSection = document.createElement('div');
            commentSection.classList.add('comment-section');
            commentSection.id = `comment-section-${postId}`;  
            
            const commentsList = document.createElement('div');
            commentsList.id = `comments-list-${postId}`;  

          
            const commentInput = document.createElement('input');
            commentInput.id = `comment-input-${postId}`;  
            commentInput.placeholder = 'Add a comment...';

            const commentButton = document.createElement('button');
            commentButton.textContent = 'Add Comment';
            commentButton.onclick = function() {
                addComment(postId);  
            };

            
            commentSection.appendChild(commentsList);
            commentSection.appendChild(commentInput);
            commentSection.appendChild(commentButton);

            
            postContainer.appendChild(imgElement);
            postContainer.appendChild(captionElement);
            postContainer.appendChild(likeButton);
            postContainer.appendChild(likeCountSpan);
            postContainer.appendChild(commentSection);

            
            document.getElementById('posts-container').appendChild(postContainer);

           
            document.getElementById('postForm').reset();

           
        } else {
            alert('Error uploading post.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error uploading post');
    });
});

document.getElementById('load-posts-btn').addEventListener('click', function() {
    const userId = localStorage.getItem('userId'); 
    fetch(`/friendsbook/userPosts?userId=${userId}`)
        .then(response => response.json())
        .then(posts => {
            const postsContainer = document.getElementById('posts-container');
            postsContainer.innerHTML = ''; 

            posts.forEach(post => {
                const postElement = document.createElement('div');
                const imgElement = document.createElement('img');
                imgElement.src = `/friendsbook/post/${post.postId}`; 
                imgElement.style.maxWidth = '300px';

                const captionElement = document.createElement('p');
                captionElement.textContent = `${userId} : ${post.caption}` || 'No caption';

                
                const likeButton = document.createElement('button');
                likeButton.classList.add('like-button');
                likeButton.setAttribute('onclick', `likePost(${post.postId})`);
				likeButton.textContent = `Like ${post.likeCount}`;
                const likeCount = document.createElement('span');
                likeCount.classList.add('like-count');
                likeCount.id = `like-count-${post.postId}`;

             
                const likes = Array.isArray(post.likes) ? post.likes : [];
             
                
                const commentSection = document.createElement('div');
                commentSection.id = `comments-section-${post.postId}`;

                const commentInput = document.createElement('input');
                commentInput.type = 'text';
                commentInput.placeholder = 'Add a comment...';
                commentInput.id = `comment-input-${post.postId}`;

                const commentButton = document.createElement('button');
                commentButton.textContent = 'Add Comment';
                commentButton.onclick = function() {
                    addComment(post.postId);
                };

               
                commentSection.appendChild(commentInput);
                commentSection.appendChild(commentButton);

               
                const commentsList = document.createElement('div');
                commentsList.id = `comments-list-${post.postId}`;
                commentSection.appendChild(commentsList);

                postElement.appendChild(imgElement);
                postElement.appendChild(captionElement);
                postElement.appendChild(likeButton);
                postElement.appendChild(likeCount);
                postElement.appendChild(commentSection);
                postsContainer.appendChild(postElement);
				
	            fetchComments(post.postId, userId);
            });
        })
        .catch(error => console.error('Error fetching posts:', error));
});




function fetchFollowersCount() {
	const userId=localStorage.getItem('userId')
        fetch(`/friendsbook/${userId}/followers/count`)
            .then(response => response.json())
            .then(data => {
             
                document.getElementById("followersCount").innerText = data;
            })
            .catch(error => console.error('Error fetching followers count:', error));
    }


    function fetchFollowingsCount() {
		const userId=localStorage.getItem('userId')
        fetch(`/friendsbook/${userId}/followings/count`)
            .then(response => response.json())
            .then(data => {
               
                document.getElementById("followingsCount").innerText = data;
            })
            .catch(error => console.error('Error fetching followings count:', error));
    }

	function fetchPostCount(){
		const userId=localStorage.getItem('userId')
		fetch(`/friendsbook/posts/count/${userId}`)
		        .then(response => {
		            if (!response.ok) {
		                throw new Error('Failed to fetch post count');
		            }
		            return response.json(); 
		        })
		        .then(postCount => {
		          
		            document.getElementById('postsCount').textContent = postCount;
		        })
		        .catch(error => {
		            console.error('Error fetching post count:', error);
		            alert('Failed to fetch post count');
		        });
		}
		
    
    window.onload = function() {
		const userId=localStorage.getItem('userId');
        fetchFollowersCount();
        fetchFollowingsCount();
		fetchPostCount();
		fetchPostsFromFollowings(userId);
    };
	document.addEventListener("DOMContentLoaded", function() {
		const userId=localStorage.getItem('userId');
	    fetchPostsFromFollowings(userId);
	});
	document.getElementById('followButton').addEventListener('click', function() {
	    const userId = localStorage.getItem('userId'); 
		fetchFollowers(userId);
	});

		function fetchFollowers(userId) {
		    console.log("Fetching followers for user: " + userId);
		    
		    fetch(`/friendsbook/followers/${userId}`)
		        .then(response => {
		            if (!response.ok) {
		                throw new Error("Failed to fetch followers");
		            }
		            return response.json();
		        })
		        .then(followers => {
		            
		            const followersList = document.getElementById('followersList');
		            followersList.innerHTML = '';  

		            
		            if (followers.length > 0) {
		                followers.forEach(follower => {
		                    const listItem = document.createElement('li');
		                    listItem.textContent = `User ID: ${follower.userId}`;

		                   
		                    checkIfFollowing(userId, follower.userId, listItem);
		                    
		                  
		                    followersList.appendChild(listItem);
		                });
		            } else {
		                const noFollowersMessage = document.createElement('li');
		                noFollowersMessage.textContent = "You have no followers.";
		                followersList.appendChild(noFollowersMessage);
		            }
		        })
		        .catch(error => {
		            console.error("Error fetching followers:", error);
		            document.getElementById('followers-error-message').style.display = 'block';
		        });
		}

		function checkIfFollowing(followerId, followedUserId, listItem) {
		    fetch(`/friendsbook/following/exists?followerId=${followerId}&followedUserId=${followedUserId}`)
		        .then(response => response.json())
		        .then(isFollowing => {
		            if (isFollowing) {
		              
		                const unfollowButton = createButton('Unfollow', () => unfollowUser(followerId, followedUserId));
		                listItem.appendChild(unfollowButton);
		            } else {
		               
		                checkFollowRequest(followerId, followedUserId, listItem);
		            }
		        })
		        .catch(error => console.error("Error checking follow status:", error));
		}

		function checkFollowRequest(followerId, followedUserId, listItem) {
		    fetch(`/friendsbook/notifications/check/follow?senderId=${followerId}&recipientId=${followedUserId}`)
		        .then(response => response.json())
		        .then(isRequested => {
		            if (isRequested) {
		              
		                const requestedButton = createButton('Requested', () => {});
		                listItem.appendChild(requestedButton);
		            } else {
		                
		                const followButton = createButton('Follow', () => sendFriendRequest(followerId, followedUserId));
		                listItem.appendChild(followButton);
		            }
		        })
		        .catch(error => console.error("Error checking follow request:", error));
		}

		function createButton(text, onClick) {
		    const button = document.createElement('button');
		    button.textContent = text;
		    button.className = 'follow-btn';
		    button.onclick = onClick;
		    return button;
		}

	document.getElementById('followingButton').addEventListener('click', function() {
		    const userId = localStorage.getItem('userId');  
			fetchFollowings(userId);
		});
		
		async function sendFriendRequest(userId,followerId) {
		    const senderId = userId; 
		   const recipientId = followerId;
		  console.log(senderId);
		  console.log(recipientId);
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
					if(followButton){
		            followButton.textContent = "Request Sent";
		            followButton.disabled = true;  
		            alert(result);
		        } else {
		            alert(result);  
		        }
		    }
			}
			 catch (error) {
		        console.error("Error sending friend request:", error);
		        alert("Error sending friend request.");
		    }
		}
		
		
		function fetchFollowings(userId) {
		    console.log("Fetching followings for user: " + userId);
		    
		    fetch(`/friendsbook/followings/${userId}`)
		        .then(response => {
		            if (!response.ok) {
		                throw new Error("Failed to fetch followings");
		            }
		            return response.json(); 
		        })
		        .then(followings => {
		            const followingsList = document.getElementById('followingsList');  
		            
		            followingsList.innerHTML = '';  

console.log(followings.length);
		           
		            if (followings.length > 0) {
		                followings.forEach(following => {
		                    const listItem = document.createElement('li');
		                    listItem.className = 'following-item';
		                    listItem.textContent = `User ID: ${following.userId}`;  
							
							                    const unfollowButton = document.createElement('button');
							                    unfollowButton.textContent = 'Unfollow';
							                    unfollowButton.className = 'unfollow-btn';
							                    unfollowButton.onclick = function() {
							                        unfollowUser(userId, following.userId); 
							                    };

							                    listItem.appendChild(unfollowButton);
							
							 followingsList.appendChild(listItem);
		                });
		                followingsList.style.display = 'block'; 
		            } else {
						const noFollowersMessage = document.createElement('li');
							                noFollowingsMessage.textContent = "You have no followings.";
							                followingsList.appendChild(noFollowingsMessage);
		            }
		        })
		        .catch(error => {
		            console.error("Error fetching followings:", error);

		           
		            const errorMessageElement = document.getElementById('following-error-message');
		            if (errorMessageElement) {
		                errorMessageElement.style.display = 'block';
		            } else {
		                console.error("Error message element not found.");
		            }
		        });
		}

		function addComment(postId){
					const commentContent = document.getElementById(`comment-input-${postId}`).value;
					if (!commentContent) {
						alert("Please write a comment.");
						return;
					}
					const userId =localStorage.getItem('userId'); 
					$.post(`/friendsbook/addcomment/${postId}/${userId}/${encodeURIComponent(commentContent)}`, function(response) {
		                if (response === "Comment add successfully") {
		                    alert(response);
		                    fetchComments(postId , userId);
		                } else {
		                    alert("fail to add comment ");
		                }
		            });
				}
				
				
						function fetchComments(postId, userId) {
							console.log("inside fetchcomment");
						    fetch(`/friendsbook/getComment/${postId}`)
						        .then(response => response.json())
						        .then(comments => {
									console.log("Raw response data:",comments); 


						            const commentsList = document.getElementById(`comments-list-${postId}`);

						            if (!commentsList) {
						                console.error(`Comments list not found for postId: ${postId}`);
						                return;
						            }

						            commentsList.innerHTML = '';  

						           
						            comments.forEach(comment => {
						                const commentElement = document.createElement('p');
						                commentElement.textContent = `${comment.userId}: ${comment.content}`;
						                commentsList.appendChild(commentElement);
						            });
						        })
						        .catch(error => {
						            console.error("Error fetching comments:", error);
						            alert("Error fetching comments.");
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
            } else {
              
            }
        })
        .catch(error => {
            console.error("Error fetching posts:", error);
        });
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


document.getElementById('logout').addEventListener('click', function(){
	localStorage.clear;
	    fetch('/friendsbook/logoutCooki', {
	        headers: {
	            'Content-Type': 'application/json',
	        },
	        credentials: 'same-origin' 
	    })
	    .then(response => {
	        if (response.ok) {
	            window.location.href = "/friendsbook/login"; 
	        } else {
	            console.error("Logout failed.");
	        }
	    })
	    .catch(error => {
			console.error("Error during logout:", error);
	    });
});

								
						
