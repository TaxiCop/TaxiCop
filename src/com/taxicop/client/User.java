/* 
 * PROJECT: TaxiCop
 * --------------------------------------------------------------------------------
 *   Antonio Vanegas hpsaturn(at)gmail.com
 *   Camilo Soto cmsvalenzuela(at)gmail.com
 *   Javier Buitrago javierbuitrago123(at)gmail.com
 *   Website: http://www.taxicop.org
 *
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
 * 
 * For further information please contact.
 *  devel(at)taxicop.org
 * 
 */


package com.taxicop.client;

import android.util.Log;

import org.json.JSONObject;


public class User {

    private final String mUserName;
    private final String mFirstName;
    private final String mLastName;
    private final String mCellPhone;
    private final String mOfficePhone;
    private final String mHomePhone;
    private final String mEmail;
    private final boolean mDeleted;
    private final int mUserId;

    public int getUserId() {
        return mUserId;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getCellPhone() {
        return mCellPhone;
    }

    public String getOfficePhone() {
        return mOfficePhone;
    }

    public String getHomePhone() {
        return mHomePhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public User(String name, String firstName, String lastName,
        String cellPhone, String officePhone, String homePhone, String email,
        Boolean deleted, Integer userId) {
        mUserName = name;
        mFirstName = firstName;
        mLastName = lastName;
        mCellPhone = cellPhone;
        mOfficePhone = officePhone;
        mHomePhone = homePhone;
        mEmail = email;
        mDeleted = deleted;
        mUserId = userId;
    }

    /**
     * Creates and returns an instance of the user from the provided JSON data.
     * 
     * @param user The JSONObject containing user data
     * @return user The new instance of Voiper user created from the JSON data.
     */
    public static User valueOf(JSONObject user) {
        try {
            final String userName = user.getString("u");
            final String firstName = user.has("f") ? user.getString("f") : null;
            final String lastName = user.has("l") ? user.getString("l") : null;
            final String cellPhone = user.has("m") ? user.getString("m") : null;
            final String officePhone =
                user.has("o") ? user.getString("o") : null;
            final String homePhone = user.has("h") ? user.getString("h") : null;
            final String email = user.has("e") ? user.getString("e") : null;
            final boolean deleted =
                user.has("d") ? user.getBoolean("d") : false;
            final int userId = user.getInt("i");
            return new User(userName, firstName, lastName, cellPhone,
                officePhone, homePhone, email, deleted, userId);
        } catch (final Exception ex) {
            Log.i("User", "Error parsing JSON user object" + ex.toString());

        }
        return null;

    }

    /**
     * Represents the User's status messages
     * 
     */
    public static class Status {
        private final Integer mUserId;
        private final String mStatus;

        public int getUserId() {
            return mUserId;
        }

        public String getStatus() {
            return mStatus;
        }

        public Status(Integer userId, String status) {
            mUserId = userId;
            mStatus = status;
        }

        public static User.Status valueOf(JSONObject userStatus) {
            try {
                final int userId = userStatus.getInt("i");
                final String status = userStatus.getString("s");
                return new User.Status(userId, status);
            } catch (final Exception ex) {
                Log.i("User.Status", "Error parsing JSON user object");
            }
            return null;
        }
    }

}
