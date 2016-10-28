/*
 * Copyright 2016 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.systemone.authentication;

import se.uu.ub.cora.beefeater.authentication.User;
import se.uu.ub.cora.spider.authentication.UserInfo;
import se.uu.ub.cora.spider.authentication.UserPicker;

public class SystemOneUserPicker implements UserPicker {

	@Override
	public User pickUser(UserInfo userInfo) {
		if ("systemAdmin".equals(userInfo.idFromLogin)) {
			User user = new User("99999");
			setInfoFromUserInfo(userInfo, user);
			user.roles.add("guest");
			return user;
		}
		if ("user".equals(userInfo.idFromLogin)) {
			User user = new User("10000");
			setInfoFromUserInfo(userInfo, user);
			user.roles.add("guest");
			return user;

		}
		User user = new User("12345");
		setInfoFromUserInfo(userInfo, user);
		user.roles.add("guest");
		return user;
	}

	private void setInfoFromUserInfo(UserInfo userInfo, User user) {
		user.loginId = userInfo.idFromLogin;
		user.loginDomain = userInfo.domainFromLogin;
	}

}
