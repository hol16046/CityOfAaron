/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import cityofaaron.CityOfAaron;
import control.GameControl;
import exception.GameControlException;
import model.Game;

/**
 *
 * @author melissamoore, heatherholt, kanderson
 */
public class PlantCropsView extends ViewBase {

	/**
	 * Constructor
	 */
	public PlantCropsView() {

	}

	@Override
	protected String getMessage() {

		return "\n\nWelcome to the Granary and Storehouse!\n"
				+ "You can get seed for your fields here.\n"
				+ "To return to Manage the Crops Menu press Enter\n\n";

	}

	/**
	 * Get the set of inputs from the user.
	 *
	 * @return
	 */
	@Override
	public String[] getInputs() {

		// Declare the array to have the number of elements you intend to get 
		// from the user.
		String[] inputs = new String[1];

		inputs[0] = getUserInput("How many acres of land do you want to plant?", true);

		// Repeat for each input you need, putting it into its proper slot in the array.
		return inputs;
	}

	/**
	 * Perform the action indicated by the user's input.
	 *
	 * @param inputs
	 * @return true if the view should repeat itself, and false if the view
	 * should exit and return to the previous view.
	 */
	@Override
	public boolean doAction(String[] inputs) {
		// If the user just hits 'enter', bail out and don't do the action.
		// Returning false will take us back to the Manage Crops Menu.
		// check input from user can be converted to int call method here
		if (inputs[0] == null || inputs[0].equals("")) {
			this.console.println("\nNo amount was entered. Returning to the Manage the Crops Menu...");
			pause(2000);
			return false;
		} else {

			boolean check = false;
			int acresToPlant = 0;
			while (check == false) {
				try {
					if (inputs[0] == null || inputs[0].equals("")) {
						this.console.println("\nNo amount was entered."
								+ " Returning to the Manage the Crops Menu...");
						pause(2000);

					} else {
						acresToPlant = Integer.parseInt(inputs[0]);
						check = true;
					}

				} catch (NumberFormatException nfe) {

					ErrorView.display(this.getClass().getName(),
							"Not a valid input. Please enter a number");
					inputs = getInputs();

				}
			}

			boolean result = plantCropsTransaction(acresToPlant);
			return result;

		}

	}

	// Define your action handlers here. These are the methods that your doAction()
	// method will call based on the user's input. We don't want to do a lot of 
	// complex game stuff in our doAction() method. It will get messy very quickly.
	private boolean plantCropsTransaction(int acresToPlant) {

		Game game = CityOfAaron.getCurrentGame();

		int acresOwned = game.getAcresOwned();
		int currentPopulation = game.getCurrentPopulation();
		int totalWheat = game.getWheatInStorage();

		try {
			int crops = GameControl.plantCrops(acresToPlant, acresOwned, currentPopulation, totalWheat);
			game.setWheatInStorage(totalWheat - crops);
			game.setAcresPlanted(acresToPlant);
			this.console.println("\nYou have " + game.getWheatInStorage() + " bushels of wheat in storage.");
			pause(2000);
			return false;
		} catch (GameControlException gce) {

			ErrorView.display(this.getClass().getName(), gce.getMessage());
			return true;
		}
	}

}
