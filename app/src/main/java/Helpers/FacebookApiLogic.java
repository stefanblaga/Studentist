package Helpers;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mario on 16/04/2017.
 */

public class FacebookApiLogic {
    public void GetUserDetailFromFacebook(final String userRole,final String email,final String uid )
    {
        final UserApp user = new UserApp();
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            String name = response.getJSONObject().get(Constants.FbUserDetailsName).toString();
                            String birthDateString = response.getJSONObject().get(Constants.FbUserDetailsBirthday).toString();
                            String gender = response.getJSONObject().get(Constants.FbUserDetailsGender).toString();

                            user.birthDate = birthDateString;
                            user.uid = uid;
                            user.name = name;
                            user.role = userRole;

                            FirebaseLogic firebaseLogic = new FirebaseLogic();
                            firebaseLogic.WriteUserToFirebase(user);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("xxx_mario_xxx","response from fb: "  + response.getJSONArray());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString(Constants.FbFields, Constants.FbUserDetailsName + "," + Constants.FbUserDetailsBirthday + "," + Constants.FbUserDetailsGender);
        request.setParameters(parameters);
        request.executeAsync();
    }

}
