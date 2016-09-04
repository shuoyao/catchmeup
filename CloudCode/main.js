Parse.serverURL = "http://catchmeupmdb.herokuapp.com/parse/";
/*
getFreeTimes: takes in a list of string dates that look like this "102000001230-12312412312" (two ints with a - in between), 
which are the list of user free times and returns a list of freetimes for both people.
friendName - the username of the person who you want to find freetimes with
selfTimes - your own list of freeTimes, formatted as a String array of format above.
@return - list of Start, End Date Object Pairs in hour long blocks Ex: [[Date1, Date2], [Date3, Date4]]
*/
Parse.Cloud.define("getFreeTimes", function(request, response) {
  var query = new Parse.Query(Parse.User);
  query.equalTo("username", request.params.friendName);
  query.first({	
		success: function(results) {
	        var emptyTimes = [];
	        var friendTimes = results.get("freeTimes");
	        var friendCounter = 0;
	        var userCounter = 0;
	        var selfTimes = request.params.selfTimes;
	        while (friendCounter < friendTimes.length && userCounter < selfTimes.length) {
			    var self = parseDate(String(selfTimes[userCounter]));
			    var friend = parseDate(String(friendTimes[friendCounter]));
			    selfStart = self[0].getTime();
			    selfEnd = self[1].getTime();
			    friendStart = friend[0].getTime();
			    friendEnd = friend[1].getTime();
			    var temp = [];
			    if (selfStart <= friendStart && selfEnd >= friendEnd) { // you start before they start, you end after they end
			        temp.push(friend[0]);
			        temp.push(friend[1]);
			        emptyTimes.push(temp);
			        friendCounter++;
			    }
			    else if (selfStart >= friendStart && selfEnd  <= friendEnd) { // they start before you start, they end after you end
			        temp.push(self[0]);
			        temp.push(self[1]);
			        emptyTimes.push(temp);
			        userCounter++;
			    }
			    else if (selfStart < friendEnd && selfEnd > friendEnd) { // you start before they end, you end after they end
			        temp.push(self[0]);
			        temp.push(friend[1]);
			        emptyTimes.push(temp);
			        friendCounter++;
			    }
			    else if (selfStart < friendStart && selfEnd > friendStart) { // you start before they start, you end after they start
			    	temp.push(friend[0]);
			        temp.push(self[1]);
			        emptyTimes.push(temp);
			        userCounter++;
			    }
			    else if (selfStart > friendEnd) { // if the user start time is later than the friend end time
			        friendCounter++;
			    }
			    else if (selfEnd  < friendStart) {// if the user end time is earlier than the friend start time
			        userCounter++;
			    }
			    else if (selfStart == friendStart && selfEnd == friendEnd) {
			        var temp = [];
			        temp.push(self[0]);
			        temp.push(self[1]);
			        emptyTimes.push(temp);
			        friendCounter++;
			        userCounter++;
			    }
			}
	        response.success((separateBlocks(weedDates(emptyTimes, request.params.timeDiff))));
    },
    error: function() {
    	var emptyArray = [[]];
        response.error(emptyArray);
    }
  });
});
/*
parseDate: takes in a DateString of form "1204213123-1231243123" and returns two dates
DateString - the string that represents a date range
@return - a pair of start, end date objects Ex: [Date1, Date2]
*/
function parseDate(DateString) {
    var dateList = DateString.split("-");
    var result = [];
    var d1 = new Date(parseInt(dateList[0]));
    var d2 = new Date(parseInt(dateList[1]));
    result.push(d1);
    result.push(d2);
    return result;
}
/*
separateBlocks: takes in a list of list of Date Objects and returns a new list of list of Date Objects that are separated into one hour blocks
freeTimes - A List of List of Dates. Ex: [[Date1, Date2], [Date3, Date4]]
@return - A List of List of Dates in one hour maximum chunks
*/	
function separateBlocks(freeTimes) {
	var newArray = [];
	for (i = 0; i < freeTimes.length; i++) {
		for (j = freeTimes[i][0].getTime(); j <= freeTimes[i][1].getTime() - 3600000; j = j + 3600000) {
			var hourBlock = [];
	        var start = new Date(j);
	        var end = new Date(j + 3600000);
	        hourBlock.push(start);
	        hourBlock.push(end);
			newArray.push(hourBlock);
		}
	}
	return newArray;
}
/*
weedDates: takes in a list of list of Date Objects and returns a new list of list of Date Objects that are filtered
Filters include: No times before 9 AM or after 9 PM. All free times must be at least an hour, so a freetime from 8:30 to 9:30 will be filtered out.
Free time chunks that last multiple days are separated into multiple one day chunks
freeTimes - a list of list of Dates. Ex: [[Date1, Date2], [Date3, Date4]]
@return - a list of list of Dates that are filtered
*/	
function weedDates(freeTimes, timeDiff) {
    var weededArray = [];
    for (i = 0; i < freeTimes.length; i++) {
        var startHour = (freeTimes[i][0].getHours() + timeDiff/3600000) % 24;
        if (startHour < 0) {
            startHour = startHour + 24;
        }
        var startMinute = freeTimes[i][0].getMinutes();
        var startDate = freeTimes[i][0].getDate();
        var endHour = (freeTimes[i][1].getHours() + timeDiff/3600000) % 24;
        if (endHour < 0) {
            endHour = endHour + 24;
        }
        var endMinute = freeTimes[i][1].getMinutes();
        var endDate = freeTimes[i][1].getDate();
        var isValid = true;
        if (startHour > 20) { // 9-9 is time range for free times, need to be at least an hour long, so cannot start after 8 or end after 10
            freeTimes[i][0].setTime(freeTimes[i][0].getTime() + 86400000);
            freeTimes[i][0].setHours(9,0,0,0);
            freeTimes[i][0].setTime(freeTimes[i][0].getTime() - timeDiff);
        }
        else if (startHour < 10) {
            freeTimes[i][0].setHours(9,0,0,0);
            freeTimes[i][0].setTime(freeTimes[i][0].getTime() - timeDiff);
        }
        else if (endHour > 20) {
            freeTimes[i][1].setHours(21,0,0,0);
            freeTimes[i][1].setTime(freeTimes[i][1].getTime() - timeDiff);
        }
        else if (endHour < 10) {
            freeTimes[i][1].setTime(freeTimes[i][1].getTime() - 86400000);
            freeTimes[i][1].setHours(21,0,0,0);
            freeTimes[i][1].setTime(freeTimes[i][1].getTime() - timeDiff);
        }
        if (startDate == endDate) {
            if (endHour - startHour < 1 || (endHour - startHour == 1 && endMinute - startMinute < 0)) {
                isValid = false;
            }
            else if (freeTimes[i][0].getTime() >= freeTimes[i][1].getTime()) {
                isValid = false;
            }
            if (isValid) {
                weededArray.push(freeTimes[i]);
            }
        }
        else {
            startTime = freeTimes[i][0].getTime();
            endTime = freeTimes[i][1].getTime();
            currDateStart = new Date(freeTimes[i][0].getTime());
            currDateStart.setHours(9,0,0,0);
            currDateStart.setTime(currDateStart.getTime() - timeDiff);
            currDateEnd = new Date(freeTimes[i][0].getTime());
            currDateEnd.setHours(21, 0,0,0);
            currDateEnd.setTime(currDateEnd.getTime() - timeDiff);
            for (j = startTime; j <= endTime; j = j + 86400000) {
                var extraRange = [];
                if (j == startTime) {
                    extraRange.push(freeTimes[i][0]);
                    extraRange.push(currDateEnd);
                }
                else if (j + 86400000 >= endTime) {
                    extraRange.push(currDateStart);
                    extraRange.push(freeTimes[i][1]);
                }
                else {
                    extraRange.push(currDateStart);
                    extraRange.push(currDateEnd);
                }
                weededArray.push(extraRange);
                currDateStart = new Date(currDateStart.getTime() + 86400000);
                currDateEnd = new Date(currDateEnd.getTime() + 86400000);
            }
        }
    }
    return weededArray;
}
/*
sendFriendRequest: Takes in a the recipient of the friend request, your own email and stores a friend request in the recipient's parse row 
as well as sends a push notification to the recipient
recipientEmail - the username/email of the person receiving the friend request
senderEmail - the username/email of the person sending the friend request
@parse - will add a the username/email of the sender to the "friendRequests" column of the recipient in parse
@return - a string indicating success or error
@extra - a push notification will be sent to the recipient of the friend request
*/
Parse.Cloud.define("sendFriendRequest", function(request, response) {
	// Find devices associated with these users
	var query = new Parse.Query(Parse.User);
	query.equalTo('username', request.params.recipientEmail);
	// Find devices associated with these users
	var pushQuery = new Parse.Query(Parse.Installation);
	// need to have users linked to installations
	Parse.Cloud.useMasterKey();
	pushQuery.matchesQuery('user', query);
	var query2 = new Parse.Query("User");
	query2.equalTo('username', request.params.recipientEmail);
	query2.first({
		success: function(results) {
			var totalReq = results.get("friendRequests");
			if (totalReq == null) {
				totalReq = [];
			}
			if (totalReq.indexOf(request.params.senderEmail) == -1) {
				totalReq.push(request.params.senderEmail);
				results.set("friendRequests", totalReq);
				results.save(null, {
			        success: function(anotherUser) {
			          // The user was saved successfully.
			          response.success("Successfully updated user.");
			        },
			        error: function(results, error) {
			          // The save failed.
			          // error is a Parse.Error with an error code and description.
			          response.error("Could not save changes to user.");
			        }
			    });
			}
		},
		error: function() {
			response.error("Error");
		}
	});
	/*
	Parse.Push.send({
	    where: pushQuery,
	    data: {
		    data: {
		    	request_type: "FRIEND_REQUEST",
		    	request: {
		    		from: {
		    			email: request.params.senderEmail,
		    		},
		    		timestamp: Date.now(),
		    		message: "Friend Request from "
		    	},
		        aps: {
		            alert: "PLS",
		            //sound: ""
		        }
		    }
	    }
	}, {
	    success: function () {
	        response.success("Success!");
	    },
	    error: function (error) {
	        response.error(error);
	    }
	});
*/
});
/*
getFriendRequest: retrieves all the friend requests of the username provided
username - the username of the friend requests to retrieve
@return - a list of strings that indicate the friend requests you have
*/
Parse.Cloud.define("getFriendRequests", function(request, response) {
	var query = new Parse.Query(Parse.User);
	query.equalTo("username", request.params.username);
	query.first({
		success: function(results) {
			var requests = results.get("friendRequests");
			if (requests == null) {
				requests = []
			}
			response.success(requests);
		},
		error: function() {
			response.error(["Error"]);
		}
	});
});
/*
getName: retrieves the first and last name of the username provided
username - the username of name to retrieve
@return - a string that is the name of the corresponding username
*/
Parse.Cloud.define("getName", function(request, response) {
	var query = new Parse.Query(Parse.User);
	query.equalTo("username", request.params.username);
	query.first({
		success: function(results) {
			var name = results.get("name");
			if (name == null) {
				name = "";
			}
			response.success(name);
		},
		error: function(){
			response.error("Error");
		}
	});
});
/*
getFriends: retrieves all the friends of the username provided
username - the username of the friends to retrieve
@return - a list of strings that indicate the friends you have
*/
Parse.Cloud.define("getFriends", function(request, response) {
	var query = new Parse.Query("User");
	query.equalTo("username", request.params.username);
	query.find({	
		success: function(results) {
			var friends = results[0].get("friends");
			if (friends == null) {
				friends = [];
			}
			response.success(friends);
		},
	    error: function() {
	    	var emptyArray = [];
	        response.error(emptyArray);
	    }
    });
});
/*
searchResults: does a search on the text provided. If name, email or username match, returns a list of their name and email
query - the text to run the query on. As long as one of the three fields contain the text, the user will be part of the results
email - the email of the user doing the query
@return - a list of lists, with each list containing the name and email of a user match
*/
Parse.Cloud.define("searchResults", function(request, response) {
	var query1 = new Parse.Query("User");
	var query2 = new Parse.Query("User");
	var query3 = new Parse.Query("User");
	query1.contains("email", request.params.query);
	query2.contains("username", request.params.query);
	query3.contains("name", request.params.query);
	var query = Parse.Query.or(query1, query2, query3);
	query.find({
		success: function(results) {
			var userData = {users : []};
			for(i = 0; i < results.length; i++) {
				var currName = results[i].get("name");
				var currEmail = results[i].get("email");
				if (currEmail != request.params.email){
					userData.users.push(
						{name: currName, email: currEmail}
						);
				}
			}
			response.success(userData);
		},
		error: function() {
			response.error("Error");
		}
	});
});
/*
acceptFriendRequest: accepts a friend request for the corresponding person, removes the request from the 
friendRequests column and adds the name into the friends column
Params:
friendUsername - the username of the person who sent the friend request
selfUsername - the username of the person who accepted the friend request
@parse - upon accepting, parse will remove the friend request from the user who accepted the request, which is in the "friendRequests" column. 
Parse will also add selfUsername into friendUsername's "friends" column and vice versa
@return - a string that indicates success/failure
*/
Parse.Cloud.define("acceptFriendRequest", function(request, response){
	Parse.Cloud.useMasterKey();
	var query1 = new Parse.Query(Parse.User);
	var query2 = new Parse.Query(Parse.User);
	query1.equalTo("username", request.params.friendUsername);
	query1.first({
		success: function(results) {
			var totalFriends = results.get("friends");
			var friendSettings = results.get("FriendSettings");
			var defaultFreq = results.get("defaultFrequency");
			if (totalFriends == null) {
				totalFriends = [];
			}
			else if (friendSettings == null) {
				friendSettings = [];
			}
			if (totalFriends.indexOf(request.params.selfUsername) == -1) {
				totalFriends.push(request.params.selfUsername);
				friendSettings.push([request.params.selfUsername, "-1", defaultFreq]);
				results.set("friends", totalFriends);
				results.set("FriendSettings", friendSettings);
				results.save(null, {
			        success: function(anotherUser) {
			          // The user was saved successfully.
			        },
			        error: function(results, error) {
			        	response.error(error);
			          // The save failed.
			          // error is a Parse.Error with an error code and description.
			        }
			    });
			}
		},
		error: function(){
			response.error("Error");
		}
	});
	query2.equalTo("username", request.params.selfUsername);
	query2.first({
		success: function(results) {
			var totalFriends = results.get("friends");
			var friendSettings = results.get("FriendSettings");
			var defaultFreq = results.get("defaultFrequency");
			if (totalFriends == null) {
				totalFriends = [];
			}
			else if (friendSettings == null) {
				friendSettings = [];
			}
			requests = results.get("friendRequests");
			requestIndex = requests.indexOf(request.params.friendUsername);
			if (requestIndex > -1) {
				requests.splice(requestIndex, 1);
			}
			results.set("friendRequests", requests);
			if (totalFriends.indexOf(request.params.friendUsername) == -1) {
				totalFriends.push(request.params.friendUsername);
				results.set("friends", totalFriends);
			}
			friendSettings.push([request.params.friendUsername, "-1", defaultFreq]);
			results.set("FriendSettings", friendSettings);
			results.save(null, {
		        success: function(anotherUser) {
		          // The user was saved successfully.
		          response.success("Successfully Updated");
		        },
		        error: function(results, error) {
		          // The save failed.
		          // error is a Parse.Error with an error code and description.
		        }
		    });
		},
		error: function(){
			response.error("Error");
		}
	});
});
/*
denyFriendRequest: denies a friend request for the corresponding person, removes the request from the 
friendRequests column
friendUsername - the username of the person who sent the friend request - not actually needed, but using same parameters for consistency
selfUsername - the username of the person who accepted the friend request
@parse - the friend request will be removed from selfUsername's "friendRequests" column
@return - a string indicating success or failure
*/
Parse.Cloud.define("denyFriendRequest", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.equalTo("username", request.params.selfUsername);
	query.first({
		success: function(results) {
			requests = results.get("friendRequests");
			requestIndex = requests.indexOf(request.params.friendUsername);
			if (requestIndex > -1) {
				requests.splice(requestIndex, 1);
			}
			results.set("friendRequests", requests);
			results.save(null, {
		        success: function(anotherUser) {
		          // The user was saved successfully.
		          response.success("Successfully updated!");
		        },
		        error: function(results, error) {
		          // The save failed.
		          // error is a Parse.Error with an error code and description.
		          response.error("Could not deny request.");
		        }
		    });
		},
		error: function(){
			response.error("Error");
		}
	});
});
/*
getMeetupRequest: retrieves all the meetup requests of the username provided
username - the username of the meetup requests to retrieve
@return - a list of list of strings that indicate the meetup requests you have. 
Ex: [["jimmycheung@email.com","Jimmy Cheung", "1023400001321-234123412334"], ["genjinoguchi@email.com","Genji Noguchi", "124213523-42131234"]]
Format:[[username, name, freeTime string]]
*/
Parse.Cloud.define("getMeetupRequests", function(request, response) {
	var query = new Parse.Query(Parse.User);
	query.equalTo("username", request.params.username);
	query.first({
		success: function(results) {
			var requests = results.get("meetupRequests");
			if (requests == null) {
				requests = [];
			}
			response.success(requests);
		},
		error: function() {
			response.error(["Error"]);
		}
	});
});
/*
getHistory: retrieves thhe history of the username provided
username - the username of the history to retrieve
@return - a list of list of strings that indicate the meetup requests you have. 
Ex: [["jimmycheung@email.com","Jimmy Cheung", "1023400001321-234123412334"], ["genjinoguchi@email.com","Genji Noguchi", "124213523-42131234"]]
Format:[[username, name, freeTime string]]
*/
Parse.Cloud.define("getHistory", function(request, response) {
	var query = new Parse.Query(Parse.User);
	query.equalTo("username", request.params.username);
	query.first({
		success: function(results) {
			var history = results.get("history");
			if (history == null) {
				history = [];
			}
			response.success(history);
		},
		error: function() {
			response.error(["Error"]);
		}
	});
});
/*
sendMeetupRequest: Takes in a the recipient of the meetup request, your own email, and the time of the meetup in string format and stores a meetup request in the recipient's parse row 
as well as sends a push notification to the recipient
recipientEmail - the username/email of the person receiving the meetup request
senderEmail - the username/email of the person sending the meetup request
senderName - the name of the person sending the meetup request
freeTimes - the String representation of the range of times avaliable to meet up
@parse - will add a the username/email of the sender to the "meetupRequests" column of the recipient in parse
@return - a string indicating success or error
@extra - a push notification will be sent to the recipient of the meetup request
*/
Parse.Cloud.define("sendMeetupRequest", function(request, response) {
	// Find devices associated with these users
	var query = new Parse.Query(Parse.User);
	query.equalTo('username', request.params.recipientEmail);
	// Find devices associated with these users
	var pushQuery = new Parse.Query(Parse.Installation);
	// need to have users linked to installations
	Parse.Cloud.useMasterKey();
	pushQuery.matchesQuery('user', query);
	var query2 = new Parse.Query("User");
	query2.equalTo('username', request.params.recipientEmail);
	query2.first({
		success: function(results) {
			var totalReq = results.get("meetupRequests");
			if (totalReq == null) {
				totalReq = [];
			}
			if (totalReq.indexOf(request.params.senderEmail) == -1) {
				var userData = [request.params.senderEmail, request.params.senderName, request.params.freeTimes];
				totalReq.push(userData);
				results.set("meetupRequests", totalReq);
				results.save(null, {
			        success: function(anotherUser) {
			        },
			        error: function(results, error) {
			          // The save failed.
			          // error is a Parse.Error with an error code and description.
			          response.error("Could not save changes to user.");
			        }
			    });
			}
		},
		error: function() {
			response.error("Error");
		}
	});
/*
	Parse.Push.send({
	    where: pushQuery,
	    data: {
		    data: {
		    	request_type: "MEETUP_REQUEST",
		    	request: {
		    		from: {
		    			email: request.params.senderEmail,
		    		},
		    		timestamp: Date.now(),
		    		message: "Meetup Request from "
		    	},
		        aps: {
		            alert: "PLS",
		            //sound: ""
		        }
		    }
	    }
	    
	}, {
	    success: function () {
	        response.success("Success!");
	    },
	    error: function (error) {
	        response.error(error);
	    }
	});
*/
});
/*
findArray: checks whether the values of an array matches the values of any array within an array of arrays
Params:
array - the array to match
arrayArray - the array of arrays
it can be assumed that the length of array is the same as the length of each array within arrayArray
@return - true or false
*/
function findArray(array, arrayArray) {
	for (i = 0; i < arrayArray.length; i++) {
		var totalResult = true;
		for (j = 0; j < arrayArray[i].length; j++) {
			if (arrayArray[i][j] != array[j]) {
				totalResult = false;
			}
		}
		if (totalResult) {
			return i;
		}
	}
	return -1;
}

/*
acceptMeetupRequest: accepts a meetup request for the corresponding person, removes the request from the 
meetupRequests column and adds the name into the friends column
Params:
friendUsername - the username of the person who sent the meetup request
friendName - the name of the person who sent the meetup request
freeTimes - the suggested freeTimes of the person who sent the request
selfUsername - the username of the person who accepted the meetup request
selfName - the name of the person who accepted the meetup request
@parse - upon accepting, parse will remove the meetup request from the user who accepted the request, which is in the "meetupRequests" column. 
Parse will also add the meetup in both friend's history columns and vice versa, if either history is > 100, remove the oldest one.
@return - a string that indicates success/failure
*/
Parse.Cloud.define("acceptMeetupRequest", function(request, response){
	Parse.Cloud.useMasterKey();
	var query1 = new Parse.Query(Parse.User);
	var query2 = new Parse.Query(Parse.User);
	query1.equalTo("username", request.params.friendUsername);
	query1.first({
		success: function(results) {
			var totalMeetups = results.get("history");
			if (totalMeetups == null) {
				totalMeetups = [];
			}
			else if (totalMeetups.length >= 100) {
				totalMeetups.shift();
			}
			var meetupRequest = [request.params.selfUsername, request.params.selfName, request.params.freeTimes];
			if (findArray(meetupRequest, totalMeetups) == -1) {
				totalMeetups.push(meetupRequest);
				results.set("history", totalMeetups);
				results.save(null, {
			        success: function(anotherUser) {
			          // The user was saved successfully.
			        },
			        error: function(results, error) {
			        	response.error(error);
			          // The save failed.
			          // error is a Parse.Error with an error code and description.
			        }
			    });
			}
		},
		error: function(){
			response.error("Error");
		}
	});
	query2.equalTo("username", request.params.selfUsername);
	query2.first({
		success: function(results) {
			var totalMeetups = results.get("history");
			if (totalMeetups == null) {
				totalMeetups = [];
			}
			else if (totalMeetups.length >= 100) {
				totalMeetups.shift();
			}
			requests = results.get("meetupRequests");
			meetupRequest = [request.params.friendUsername, request.params.friendName, request.params.freeTimes];
			requestIndex = findArray(meetupRequest, requests);
			if (requestIndex > -1) {
				requests.splice(requestIndex, 1);
			}
			results.set("meetupRequests", requests);
			if (findArray(meetupRequest, totalMeetups) == -1) {
				totalMeetups.push(meetupRequest);
				results.set("history", totalMeetups);
			}
			results.save(null, {
		        success: function(anotherUser) {
		          // The user was saved successfully.
		          response.success("Successfully Updated");
		        },
		        error: function(results, error) {
		          // The save failed.
		          // error is a Parse.Error with an error code and description.
		        }
		    });
		},
		error: function(){
			response.error("Error");
		}
	});
});
/*
denyMeetupRequest: denies a meetup request for the corresponding person, removes the request from the parse column "meetupRequests"
Params:
friendUsername - the username of the person who sent the meetup request
friendName - the name of the person who sent the meetup request
freeTimes - the suggested freeTimes of the person who sent the request
selfUsername - the username of the person who accepted the meetup request
selfName - the name of the person who accepted the meetup request
@parse - upon declining, parse will remove the meetup request from the user who accepted the request, which is in the "meetupRequests" column. 
@return - a string that indicates success/failure
*/
Parse.Cloud.define("denyMeetupRequest", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.equalTo("username", request.params.selfUsername);
	query.first({
		success: function(results) {
			requests = results.get("meetupRequests");
			meetupRequest = [request.params.friendUsername, request.params.friendName, request.params.freeTimes];
			requestIndex = findArray(meetupRequest, requests);
			if (requestIndex > -1) {
				requests.splice(requestIndex, 1);
			}
			results.set("meetupRequests", requests);
			results.save(null, {
		        success: function(anotherUser) {
		          // The user was saved successfully.
		          response.success("Successfully updated!");
		        },
		        error: function(results, error) {
		          // The save failed.
		          // error is a Parse.Error with an error code and description.
		          response.error("Could not deny request.");
		        }
		    });
		},
		error: function(){
			response.error("Error");
		}
	});
});
/*
getFriendSettings: retrieves a list of list of friend settings, formatted in the way
[[Friend Email, Frequency of Update, Current Number of Days to check frequency, name, ...], [....]]
Params:
username - the username of the person making the request
@return -the list of list of settings for each of the user's friends
*/
Parse.Cloud.define("getFriendSettings", function(request, response) {
	var query = new Parse.Query(Parse.User);
	query.equalTo("username", request.params.username);
	query.first({
		success: function(results) {
			var friendSettings = results.get("FriendSettings");
			if (friendSettings == null) {
				friendSettings = [];
			}
			response.success(friendSettings);
		},
		error: function(){
			response.error("Error");
		}
	});
});
