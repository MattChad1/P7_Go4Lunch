package com.go4lunch2.ui.list_workmates;

import static com.TestUtils.LiveDataTestUtils.getOrAwaitValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.repositories.RestaurantRepository;
import com.go4lunch2.data.repositories.UserRepository;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWorkmatesViewModelTest {

    private final List<CustomUser> users = new ArrayList<>();
    private final MutableLiveData<List<CustomUser>> workmatesWithRestaurantsLiveData = new MutableLiveData<>();

    @Mock
    UserRepository userRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    ListWorkmatesViewModel viewModel;

    String test1 = "idWorkmate";
    String test2 = "name restaurant";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        initMocks(this);
        viewModel = new ListWorkmatesViewModel(restaurantRepository, userRepository);

        users.add(new CustomUser(test1, "Mr test", "", "r1", "name restaurant", null));
        workmatesWithRestaurantsLiveData.setValue(users);

        when(userRepository.getWorkmatesWithRestaurantsLiveData()).thenReturn(workmatesWithRestaurantsLiveData);
    }

    @Test
    public void getWorkmatesViewStateItemsLiveData() throws InterruptedException {
        List<WorkmateViewStateItem> listTest =  getOrAwaitValue(viewModel.getWorkmatesViewStateItemsLiveData());
        assertEquals(listTest.get(0).getIdWorkmate(), test1);
        assertEquals(listTest.get(0).getNameRestaurant(), test2);
    }
}