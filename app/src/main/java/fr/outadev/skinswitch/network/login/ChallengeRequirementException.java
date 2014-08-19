/*
 * SkinSwitch - ChallengeRequirementException
 * Copyright (C) 2014-2014  Baptiste Candellier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.outadev.skinswitch.network.login;

/**
 * Thrown if the user has to answer a security question.
 *
 * @author outadoc
 */
public class ChallengeRequirementException extends Exception {

	private static final long serialVersionUID = 1L;
	private final LoginChallenge challenge;

	public ChallengeRequirementException(LoginChallenge challenge) {
		this.challenge = challenge;
	}

	/**
	 * Gets the challenge the user will have to answer.
	 *
	 * @return the challenge.
	 */
	public LoginChallenge getChallenge() {
		return challenge;
	}

}
