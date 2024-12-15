package com.example.androidgreenplate;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import androidx.lifecycle.LiveData;

import com.example.androidgreenplate.model.LoginStatus;
import com.example.androidgreenplate.viewmodels.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private FirebaseAuth mAuth;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}