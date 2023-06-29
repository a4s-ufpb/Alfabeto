package com.napoleao.alphabeto.controller;

import com.napoleao.alphabeto.model.Challenge;

import java.util.List;

/**
 * Class responsible for managing the manufacture of challenges.
 */
public class SelectorChallenge {

    private ManageThemes manageThemes;

    /**
     * Constructor responsible for initializing the class.
     *
     * @param challenges
     */
    public SelectorChallenge(List<Challenge> challenges) {
        this.manageThemes = new ManageThemes(challenges);
    }

    public void choosenTheme(int selectedTheme) {
        this.manageThemes.chooseTheme(selectedTheme);
    }

}
