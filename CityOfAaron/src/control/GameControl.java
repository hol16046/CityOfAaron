/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import cityofaaron.CityOfAaron;
import exception.GameControlException;
import exception.MapControlException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.Game;
import model.Map;
import model.Player;
import model.Storehouse;

/**
 *
 * @author heatherholt
 */
public class GameControl {

	public static Game createNewGame(String playerName) throws GameControlException, MapControlException {

		Player player = new Player();
		player.setName(playerName);

		Game game = new Game();
		game.setThePlayer(player);

		try {
			Map map = MapControl.createMap(5, 5);
			game.setTheMap(map);
		} catch (MapControlException mce) {
			throw new MapControlException(mce.getMessage());
		}

		CityOfAaron.setCurrentGame(game);

		Game currentGame = CityOfAaron.getCurrentGame();
		currentGame.setCurrentYear(1);
		currentGame.setCurrentPopulation(100);
		currentGame.setAcresOwned(1000);
		currentGame.setTithingPaidInBushels(300);
		currentGame.setTithingPercentage(10);
		currentGame.setWheatInStorage(2700);
		currentGame.setLandPrice(RandomNumbers.getRandom(17, 27));
		currentGame.setTheStorehouse(new Storehouse());

		return game;
	}

	public static void saveGame(Game game, String filePath) throws GameControlException, IOException {

		if (game == null || filePath == null) {
			throw new GameControlException("\nError saving Game, please try again.");
		}

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
			out.writeObject(game);
		} catch (IOException ex) {
			throw new IOException("I/O Error: " + ex.getMessage());
		}
	}

	public static Game loadGame(String filePath) throws GameControlException, IOException, ClassNotFoundException {
		if (filePath == null) {
			throw new GameControlException("\nError loading Game, please try again.");
		}

		Game game = null;
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
			game = (Game) in.readObject();
			CityOfAaron.setCurrentGame(game);
		} catch (IOException ex) {
			throw new IOException("I/O Error: " + ex.getMessage());
		} catch (ClassNotFoundException cnfe) {
			throw new ClassNotFoundException("Class Not Found: " + cnfe.getMessage());
		}

		return game;
	}

	/**
	 * Transaction to buy land, calculate cost of acres to buy
	 *
	 * @param acresToBuy user input request of acres to buy
	 * @param randomPrice random number between 17 and 27
	 * @param totalWheat total wheat in storage
	 * @throws exception.GameControlException
	 * @return
	 */
	public static int buyLand(int acresToBuy,
			int randomPrice,
			int totalWheat) throws GameControlException {
		if (acresToBuy < 0) {
			throw new GameControlException("\nNumber of acres to buy cannot be a negative number.");
		}
		int cost = (acresToBuy * randomPrice);
		if (cost > totalWheat) {
			throw new GameControlException("\nThe cost of the number of acres you"
					+ " want to buy is more than the total wheat in storage.");
		}
		return cost;
	}

	/**
	 * Transaction to sell land, calculate profit of acres sold
	 *
	 * @param acresToSell user input request of acres to sell
	 * @param randomPrice random number between 17 and 27
	 * @param totalAcres total acres owned
	 * @throws exception.GameControlException
	 * @return profit from land sold
	 */
	public static int sellLand(int acresToSell, int randomPrice, int totalAcres)
			throws GameControlException {
		if (acresToSell < 0) {
			throw new GameControlException("\nNumber of acres to sell cannot be a negative number.");
		}
		if (acresToSell > totalAcres) {
			throw new GameControlException("\nThe number of acres you want to sell"
					+ " cannot be more than the number of acres you own.");
		}
		int profit = acresToSell * randomPrice;

		return profit;
	}

	/**
	 * public int feedPeople(int bushelsFed, int totalWheat) BEGIN IF
	 * (bushelsFed < 0) THEN RETURN -1
	 * IF (bushelsFed > totalWheat) THEN RETURN -2 RETURN buselsFed END
	 *
	 * @param bushelsFed
	 * @param totalWheat
	 * @throws exception.GameControlException
	 * @return
	 */
	public static int feedPeople(int bushelsFed, int totalWheat)
			throws GameControlException {

		if (bushelsFed < 0) {
			throw new GameControlException("Number of bushels to feed people"
					+ " cannot be a negative number.");
		}

		if (bushelsFed > totalWheat) {
			throw new GameControlException("The amount of bushels entered is exceeded"
					+ " the wheat in storage.");
		}

		return bushelsFed; // validates users amount
	}

	/**
	 * public void plantCrops(int acresToPlant, int getAcresOwned, int
	 * getWheatInStorage, int getCurrent Population) BEGIN IF (acresToPlant  <  0)  THEN  RETURN -1
	 * IF (acresToPlant > getAcresOwned) THEN RETURN -2 IF (acresToPlant >
	 * getCurrentPopulation * 10) THEN RETURN -3 IF (acresToPlant / 2 >
	 * getWheatInStorage) THEN RETURN -4 setWheatInStorage = getWheatInStorage -
	 * (acresToPlant / 2) setAcresPlanted = acresToPlant END
	 *
	 * @param acresToPlant
	 * @param acresOwned
	 * @param wheatInStorage
	 * @param currentPopulation
	 * @throws exception.GameControlException
	 * @return
	 */
	public static int plantCrops(int acresToPlant,
			int acresOwned,
			int currentPopulation,
			int wheatInStorage) throws GameControlException {

		if (acresToPlant < 0) {
			throw new GameControlException("Number of acres of land to plant"
					+ " cannot be a negative number.");
		}

		if (acresToPlant > acresOwned) {
			throw new GameControlException("The amount of acres entered is more than acres owned");
		}

		if (acresToPlant > currentPopulation * 10) {
			throw new GameControlException("The amount of acres entered is more than the "
					+ "current population can take care of");
		}

		if (acresToPlant / 2 > wheatInStorage) {
			throw new GameControlException("The amount of acres entered is exceeded "
					+ "the wheat in storage");
		}

		int bushelsUsed = acresToPlant / 2;
		return bushelsUsed;
	}

	public static void liveTheYear(Game game) throws GameControlException {

		int totalWheat = game.getWheatInStorage();
		int totalPopulation = game.getCurrentPopulation();
		int percentage = game.getTithingPercentage();
		int acresToPlant = game.getAcresPlanted();
		int bushelsFed = game.getBushelsFedToPeople();
		int totalTithe = game.getTithingPaidInBushels();

		// Random Numbers
		int randomYieldLow = RandomNumbers.getRandom(1, 3); // harvestWheat
		int randomYieldMid = RandomNumbers.getRandom(2, 4); // harvestWheat
		int randomYieldHigh = RandomNumbers.getRandom(2, 5); // harvestWheat
		int randomChance = RandomNumbers.getRandom(1, 100); // wheatEatenByRats
		int randomAmountLow = RandomNumbers.getRandom(6, 10); // wheatEatenByRats
		int randomAmountMid = RandomNumbers.getRandom(3, 7); // wheatEatenByRats
		int randomAmountHigh = RandomNumbers.getRandom(3, 5); // wheatEatenByRats
		int randomGrowth = RandomNumbers.getRandom(1, 5); //peopleMoveIn

		try {
			// Beginning of harvestWheat function
			if (acresToPlant < 0) {
				throw new GameControlException("Acres to plant cannot be a negative number");
			}
			int totalHarvest = harvestWheat(acresToPlant,
					percentage,
					randomYieldLow,
					randomYieldMid,
					randomYieldHigh);
			if (totalHarvest < 0) {
				throw new GameControlException("Wheat harvested cannot be a negative number");
			}
			totalWheat = totalWheat + totalHarvest;

			//Beginning of wheatOfferings function
			totalTithe = wheatOfferings(percentage, totalHarvest);
			totalWheat = totalWheat - totalTithe;

			// Beginning of wheatEatenByRats function
			int wheatRatsAte = wheatEatenByRats(percentage,
					randomChance,
					randomAmountLow,
					randomAmountMid,
					randomAmountHigh,
					totalWheat);
			if (wheatRatsAte < 0) {
				throw new GameControlException("Wheat eaten by rats cannot be a negative number");
			}
			totalWheat = totalWheat - wheatRatsAte;

			//Beginning of populationMortality function
			int popMortality = populationMortality(bushelsFed, totalPopulation);
			totalPopulation = totalPopulation - popMortality;

			//Beginning of peopleMoveIn function
			int populationGrowth = peopleMoveIn(randomGrowth, totalPopulation);
			totalPopulation = totalPopulation + populationGrowth;

			game.setTotalWheatHarvested(totalHarvest);
			game.setTithingPaidInBushels(totalTithe);
			game.setTotalWheatRatsAte(wheatRatsAte);
			game.setPopulationDecrease(popMortality);
			game.setPopulationIncrease(populationGrowth);
			game.setCurrentPopulation(totalPopulation);
			game.setWheatInStorage(totalWheat);
			game.setCurrentYear(game.getCurrentYear() + 1);
			game.setLandPrice(RandomNumbers.getRandom(17, 27));

		} catch (GameControlException gce) {
			throw new GameControlException(gce.getMessage());
		}

	}

	/**
	 * harvestWheat
	 *
	 * @param acresToPlant user input - acres planted
	 * @param percentage tithing percentage paid
	 * @param randomYieldLow random number between 1 and 3
	 * @param randomYieldMid random number between 2 and 4
	 * @param randomYieldHigh random number between 2 and 5
	 * @return amount of wheat harvested
	 */
	public static int harvestWheat(int acresToPlant,
			int percentage,
			int randomYieldLow,
			int randomYieldMid,
			int randomYieldHigh) throws GameControlException {
		if (percentage < 0 || percentage > 100) {
			throw new GameControlException("\nThe percentage of tithing paid must be between 0 and 100.");
		}
		if (acresToPlant < 0) {
			throw new GameControlException("\nAcres to plant cannot be a negative number.");
		}
		int randomYield = 0;
		if (percentage < 8) {
			randomYield = randomYieldLow;
		} else if (percentage >= 8 && percentage <= 12) {
			randomYield = randomYieldMid;
		} else if (percentage > 12) {
			randomYield = randomYieldHigh;
		}
		int wheatHarvested = acresToPlant * randomYield;
		return wheatHarvested;
	}

	/**
	 * wheatOfferings - Calculate the number of bushels to pay in tithing
	 *
	 * @param percentage User input tithing percentage
	 * @param totalHarvest Total bushels harvested after live the year
	 * @throws exception.GameControlException
	 * @return Number of bushels to pay in tithing
	 */
	public static int wheatOfferings(int percentage, int totalHarvest)
			throws GameControlException {
		if (percentage < 0 || percentage > 100) {
			throw new GameControlException("\nYou cannot offer less than 0% or more than 100%.");
		}
		int offeringsTithes = (int) (totalHarvest * (percentage * 0.01));
		return offeringsTithes;
	}

	/**
	 * Calculate the amount of wheat eaten by rats
	 *
	 * @param percentage percentage of tithing paid
	 * @param randomChance random number between 1 and 100
	 * @param randomAmountLow random number between 6 and 10
	 * @param randomAmountMid random number between 3 and 7
	 * @param randomAmountHigh random number between 3 and 5
	 * @param totalWheat total wheat in storage
	 * @return amount of wheat eaten by rats
	 */
	public static int wheatEatenByRats(int percentage,
			int randomChance,
			int randomAmountLow,
			int randomAmountMid,
			int randomAmountHigh,
			int totalWheat) throws GameControlException {
		if (percentage < 0 || percentage > 100) {
			throw new GameControlException("\nThe percentage of tithing paid must be between 0 and 100.");
		}
		int randomAmount = 0;
		int wheatEaten = 0;
		if (randomChance < 30) {
			if (percentage < 8) {
				randomAmount = randomAmountLow;
			} else if (percentage >= 8 && percentage <= 12) {
				randomAmount = randomAmountMid;
			} else if (percentage > 12) {
				randomAmount = randomAmountHigh;
			}
			wheatEaten = (int) (totalWheat * (randomAmount * 0.01));
		}
		return wheatEaten;
	}

	/**
	 * public int populationMortality (int bushelFed, int totalPopulation) BEGIN
	 * IF (bushelFed < 0 ) THEN RETURN -1 int noPeopleFed = bushelFed / 20 int
	 * starvedPeople = 0 IF (noPeopleFed < totalPopulation) THEN starvedPeople =
	 * totalPopulation – noPeopleFed RETURN starvedPeople END @param bushelsFed
	 * @param totalPopulation @param bushelsFed @throws
	 * exception.GameControlException
	 * @re
	 *
	 * t
	 * u
	 * r
	 * n
	 */
	public static int populationMortality(int bushelsFed, int totalPopulation)
			throws GameControlException {

		if (bushelsFed < 0) {
			throw new GameControlException("Number of bushels to feed people"
					+ " cannot be a negative number.");
		}

		int noPeopleFed = bushelsFed / 20;
		int starvedPeople = 0;

		if (noPeopleFed < totalPopulation) {
			starvedPeople = totalPopulation - noPeopleFed;
		}

		return starvedPeople;

	}

	/**
	 * peopleMoveIn - Calculate the number of people that moved in
	 *
	 * @param randomGrowth random number generation between 1 and 5
	 * @param totalPopulation population prior to live the year
	 * @throws exception.GameControlException
	 * @return number of people that moved in
	 */
	public static int peopleMoveIn(int randomGrowth, int totalPopulation)
			throws GameControlException {
		if (randomGrowth < 1 || randomGrowth > 5) {
			throw new GameControlException("\nThe population must grow between 1-5%."
					+ " The game has made an error.");
		}
		int growPopulation = (int) (totalPopulation * (randomGrowth * 0.01));
		return growPopulation;
	}

}
