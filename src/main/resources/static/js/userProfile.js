
     function loadProfileImage(userId) {
      
       fetch(`/friendsbook/images/profilephoto?userId=${userId}`)
         .then(response => {
           if (response.ok) {
             return response.text();  
           }
           throw new Error('Network response was not ok');
         })
         .then(data => {
           
           const profileImageUrl = data;  
           
           const profileImageElement = document.getElementById('profile-image');
		   const contextPath = '/friendsbook';
           profileImageElement.src = contextPath+profileImageUrl;
         })
         .catch(error => {
           console.error('There was a problem with the fetch operation:', error);
         });
     }

   
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
	const userId = localStorage.getItem('userId');  
	loadProfileImage(userId);
	fetchFollowersCount();
	fetchFollowingsCount();
	fetchPostCount();
	};
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
		document.getElementById('followingButton').addEventListener('click', function() {
			    const userId = localStorage.getItem('userId');  
				fetchFollowings(userId);
			});
			
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
					                    followingsList.appendChild(listItem);
					                });
					                followingsList.style.display = 'block'; 
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
					
				
					document.getElementById('load-post').addEventListener('click', function() {
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
					function addComment(postId){
										const commentContent = document.getElementById(`comment-input-${postId}`).value;
										if (!commentContent) {
											alert("Please write a comment.");
											return;
										}
										const currentUserId =localStorage.getItem('currentUserId'); 
										$.post(`/friendsbook/addcomment/${postId}/${currentUserId}/${encodeURIComponent(commentContent)}`, function(response) {
							                if (response === "Comment add successfully") {
							                    alert(response);
							                    fetchComments(postId , currentUserId);
							                } else {
							                    alert("fail to add comment "); 
							                }
							            });
									}
									function likePost(postId) {
									    console.log("Inside likePost");
									    const currentUserId= localStorage.getItem('currentUserId'); 
									    
									    $.post(`/friendsbook/like/${postId}/${currentUserId}`, function(response) {
									        if (response === "Post liked successfully!") {
									           
									            updateLikeCount(postId);
									          
									            sendLikeNotification(postId, currentUserId);  
												alert("post liked successfully");
									        }
											else {
									            alert(response); 
									        }
									    });
									}


									function sendLikeNotification(postId, currentUserId) {
									    $.post(`/friendsbook/notifications/sendLikeNotification`, {
									        postId: postId,
									        senderId: currentUserId
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

										function fetchComments(postId, currentUserId) {
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