package com.matchify.service.implementation;

import com.matchify.config.JwtProvider;
import com.matchify.dto.response.FetchInterestResponse;
import com.matchify.dto.request.FillInterestRequest;
import com.matchify.dto.response.FillInterestResponse;
import com.matchify.dto.response.InterestCategoryResponse;
import com.matchify.exception.InvalidUserArgumentsException;
import com.matchify.model.InterestCategory;
import com.matchify.model.User;
import com.matchify.model.UserInterest;
import com.matchify.repository.InterestRepository;
import com.matchify.repository.UserInterestRepository;
import com.matchify.repository.UserRepository;
import com.matchify.service.AuthService;
import com.matchify.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InterestServiceImpl implements InterestService {
    @Autowired
    private UserInterestRepository userInterestRepository;
    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    public InterestServiceImpl(UserInterestRepository userInterestRepository, InterestRepository interestRepository, UserRepository userRepository, AuthService authService) {


    }


    @Override
    public FillInterestResponse insertInterest(FillInterestRequest fillInterestRequest) {
        List<Integer> selectedGroups = fillInterestRequest.getGroups();
        List<Integer> selectedCategories = fillInterestRequest.getCategories();

        if (selectedGroups.isEmpty() && selectedCategories.isEmpty()) {
            throw new InvalidUserArgumentsException("Please select at least one category or group");
        }

        // if selectedGroups is empty
        if (!selectedGroups.isEmpty()) {
            for (Integer groupId : selectedGroups) {
                selectedCategories.addAll(getInterestGroupIds(groupId));
            }
        }

        // do a unique value check on selectedCategories
        selectedCategories = selectedCategories.stream().distinct().collect(Collectors.toList());

        Integer userId = authService.getUserIdFromToken();

        for (Integer interestId : selectedCategories) {
            UserInterest userInterest = new UserInterest();
            userInterest.setUserId(userId);
            userInterest.setInterestId(interestId);
            userInterestRepository.save(userInterest);
        }

        FillInterestResponse fillInterestResponse = new FillInterestResponse();
        fillInterestResponse.setMsg("Interest saved successfully");
        return fillInterestResponse;
    }

    private List<Integer> getInterestCategoriesIds() {
        return interestRepository.findAll().stream()
                .map(InterestCategory::getId).
                collect(Collectors.toList());
    }

    /**
     * Get interest ids by group id
     *
     * @param groupId group id
     * @return list of interest ids
     */
    private List<Integer> getInterestGroupIds(Integer groupId) {
        List<InterestCategory> interestCategories = interestRepository.findAllByGroupId(groupId);
        List<Integer> interestIds = extractInterestIds(interestCategories);
        List<Integer> distinctInterestIds = filterDuplicateIds(interestIds);

        return distinctInterestIds;
    }

    /**
     * Extract interest ids from interest categories
     *
     * @param interestCategories list of interest categories
     * @return list of interest ids
     */
    private List<Integer> extractInterestIds(List<InterestCategory> interestCategories) {
        return interestCategories.stream()
                .map(InterestCategory::getId)
                .collect(Collectors.toList());
    }

    /**
     * Filter duplicate interest ids
     *
     * @param interestIds list of interest ids
     * @return list of distinct interest ids
     */
    private List<Integer> filterDuplicateIds(List<Integer> interestIds) {
        return interestIds.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<InterestCategory> getInterestCategoriesByGroupId(Integer groupId) {
        return interestRepository.findAllByGroupId(groupId);
    }

    /**
     * Group interest categories by group ID
     *
     * @param interestCategories list of interest categories
     * @return list of FetchInterestResponse objects
     */
    public List<FetchInterestResponse> groupItemsByGroupId(List<InterestCategory> interestCategories) {
        // Group interest categories by group ID
        Map<Integer, List<InterestCategory>> groupedItems = groupInterestCategoriesByGroupId(interestCategories);

        // Map grouped items to FetchInterestResponse objects
        return mapGroupedItemsToResponses(groupedItems);
    }

    /**
     * Group interest categories by group ID
     *
     * @param interestCategories list of interest categories
     * @return map of grouped items
     */
    private Map<Integer, List<InterestCategory>> groupInterestCategoriesByGroupId(List<InterestCategory> interestCategories) {
        return interestCategories.stream()
                .collect(Collectors.groupingBy(InterestCategory::getGroupId));
    }

    /**
     * Map grouped items to FetchInterestResponse objects
     *
     * @param groupedItems grouped items
     * @return list of FetchInterestResponse objects
     */
    private List<FetchInterestResponse> mapGroupedItemsToResponses(Map<Integer, List<InterestCategory>> groupedItems) {
        return groupedItems.entrySet().stream()
                .map(entry -> {
                    FetchInterestResponse response = new FetchInterestResponse();

                    response.setGroupId(entry.getKey());
                    List<InterestCategory> items = entry.getValue();
                    if (!items.isEmpty()) {
                        response.setGroupName(items.get(0).getGroupName());
                    }

                    List<InterestCategoryResponse> categories = mapItemsToCategoryResponses(items);
                    response.setCategories(categories);

                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Map interest categories to interest category responses
     *
     * @param items list of interest categories
     * @return list of interest category responses
     */
    private List<InterestCategoryResponse> mapItemsToCategoryResponses(List<InterestCategory> items) {
        return items.stream()
                .map(this::mapToInterestCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map interest category to interest category response
     *
     * @param item interest category
     * @return interest category response
     */
    private InterestCategoryResponse mapToInterestCategoryResponse(InterestCategory item) {
        return InterestCategoryResponse.builder()
                .id(item.getId())
                .name(item.getCategoryName())
                .build();
    }


    @Override
    public List<FetchInterestResponse> fetchInterest() {
        FetchInterestResponse fetchInterestResponse = new FetchInterestResponse();

        return groupItemsByGroupId(interestRepository.findAll());
    }
}
