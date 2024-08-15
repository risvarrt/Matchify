package com.matchify.service.implementation;

import com.matchify.config.MatchConstant;
import com.matchify.dto.UserProfile;
import com.matchify.dto.response.FindMatchesResponse;
import com.matchify.exception.UserNotFoundException;
import com.matchify.model.User;
import com.matchify.model.UserInterest;
import com.matchify.model.UserMatches;
import com.matchify.repository.UserInterestRepository;
import com.matchify.repository.UserMatchesRepository;
import com.matchify.repository.UserRepository;
import com.matchify.service.AuthService;
import com.matchify.service.MatchService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MatchServiceImpl implements MatchService {

  @Autowired
  private UserInterestRepository userInterestRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthService authService;

  @Autowired
  private UserMatchesRepository userMatchesRepository;

  /**
   * Finds matches for the current authenticated user.
   *
   * @return FindMatchesResponse containing the list of matched user profiles.
   */
  @Override
  public FindMatchesResponse findMatches() {
    //Check if the user exists
    if(authService.getUserIdFromToken() == null)
      throw new UserNotFoundException("User not exists");

    Integer userId=authService.getUserIdFromToken();

    List<Integer> interestIds = userInterestRepository.findUserInterests(userId);

    Map<Integer, Integer> topUserIds = findMatchingUsers(interestIds, userId);

    for (Integer matchedId : topUserIds.keySet()) {
      if (!matchExists(userId, matchedId)) {
        storeMatch(userId, matchedId);
      }
    }

    return getMatchedUsersProfile(topUserIds);
  }

  /**
   * Finds users with matching interests.
   *
   * @param interestIds List of interest IDs for the current user.
   * @param currentUserId The current User object.
   * @return FindMatchesResponse with the matched users.
   */
  public Map<Integer, Integer> findMatchingUsers(List<Integer> interestIds, Integer currentUserId) {
    // Retrieves all user interests from the repository.
    List<UserInterest> allUserInterests = userInterestRepository.findAll();

    // Removes the current user's interests to avoid matching with oneself.
    allUserInterests.removeIf(userInterest -> userInterest.getUserId().equals(currentUserId));

    // Maps user IDs to their count of matching interests.
    Map<Integer, Integer> userIdToMatchingCount = calculateMatchingInterests(allUserInterests, interestIds);

    return getTopMatchingUsers(userIdToMatchingCount);
  }

  private Map<Integer, Integer> calculateMatchingInterests(List<UserInterest> allUserInterests, List<Integer> interestIds) {
    return allUserInterests.stream()
        .filter(ui -> interestIds.contains(ui.getInterestId()))
        .collect(Collectors.toMap(UserInterest::getUserId, ui -> 1, Integer::sum));
  }

  // Retrieves the top 5 user IDs with the highest count of matching interests.
  private Map<Integer, Integer> getTopMatchingUsers(Map<Integer, Integer> userIdToMatchingCount) {
    return userIdToMatchingCount.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(MatchConstant.topMatchesCount)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }
  /**
   * Retrieves profiles of users with matching interests.
   *
   * @param topUserIds List of user IDs with the highest matching interest counts.
   * @return FindMatchesResponse containing the profiles of matched users.
   */
  public FindMatchesResponse getMatchedUsersProfile(Map<Integer, Integer> topUserIds) {
    List<UserProfile> matchedUsers = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry : topUserIds.entrySet()) {
      User matchedUser = userRepository.findById(entry.getKey())
          .orElseThrow(() -> new RuntimeException("User not found with ID: " + entry.getKey()));

      UserProfile userProfile = new UserProfile();
      userProfile.setUserId(matchedUser.getUserId());
      userProfile.setName(matchedUser.getFirstName() + " " + matchedUser.getLastName());
      userProfile.setLocation(matchedUser.getLocation());
      userProfile.setAgeRange(matchedUser.getAgeRange());
      userProfile.setSimilarityScore(entry.getValue());
      matchedUsers.add(userProfile);
    }
    return new FindMatchesResponse(matchedUsers);
  }

  public boolean matchExists(Integer userId1, Integer userId2) {
    // Implement logic to check if a match between userId1 and userId2 exists
    return userMatchesRepository.existsByUser_UserIdAndMatchedUser_UserId(userId1, userId2);
  }

  //Storing the matches in the database
  public void storeMatch(Integer userId1, Integer userId2) {
    UserMatches match = new UserMatches();
    match.setUser(userRepository.findById(userId1).orElseThrow());
    match.setMatchedUser(userRepository.findById(userId2).orElseThrow());
    userMatchesRepository.save(match);
  }
}
