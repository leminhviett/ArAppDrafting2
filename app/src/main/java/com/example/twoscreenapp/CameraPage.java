package com.example.twoscreenapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.util.Collection;
import java.util.Iterator;

public class CameraPage extends AppCompatActivity {
    private ModelRenderable modelRenderable;
    private ModelRenderable modelRenderable1;
    private Texture texture;
    private boolean isAdded = false;
    private boolean trigger1 = true;
    private boolean trigger2 = true;

    private AugmentedFaceNode[] augmentedFaceNodes = new AugmentedFaceNode[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_page);
        CustomArFragment customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        //Load models
        //R.raw.fox_face will go to res/raw/fox_face
        // to render the 3d object. load 3d content into sceneform
        ModelRenderable.builder()
                .setSource(this, R.raw.glasses_model)
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);
                });
        ModelRenderable.builder()
                .setSource(this, R.raw.fox_face1)
                .build()
                .thenAccept(renderable -> {
                    modelRenderable1 = renderable;
                    modelRenderable1.setShadowCaster(false);
                    modelRenderable1.setShadowReceiver(false);
                });
        // Load the face mesh texture.
        Texture.builder()
                .setSource(this, R.drawable.fox_face_mesh_texture)
                .build()
                .thenAccept(texture -> this.texture = texture);

//<----------------------------------------------------------------------------------------------------->
        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        customArFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        //OnUpdateListener --> Interface definition for a callback to be invoked once per frame immediately before the scene is updated
        customArFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if(modelRenderable == null || texture == null|| modelRenderable1 == null)
                return;
            Frame frame = customArFragment.getArSceneView().getArFrame();
            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            for(AugmentedFace augmentedFace : augmentedFaces) {
                //Do your rendering work with the face data
                if(isAdded)
                    return;
                // Create a face node and add it to the scene
                //Create an AugmentedFaceNode with the given AugmentedFace.
                AugmentedFaceNode augmentedFaceNode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode.setParent(customArFragment.getArSceneView().getScene());
                augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);
                augmentedFaceNode.setFaceMeshTexture(texture);

                AugmentedFaceNode augmentedFaceNode1 = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode1.setParent(customArFragment.getArSceneView().getScene());
                //Overlay the 3D assets on face
                augmentedFaceNode1.setFaceRegionsRenderable(modelRenderable1);
//              Overylay the texture on face

                augmentedFaceNodes[0] = augmentedFaceNode;
                augmentedFaceNodes[1] = augmentedFaceNode1;

                isAdded = true;
            }
        });
    }

    public void trigger1(View v) {
        trigger1 = !trigger1;
        if (!trigger1) {
            augmentedFaceNodes[0].setFaceMeshTexture(null);
            augmentedFaceNodes[0].setFaceRegionsRenderable(null);
        } else {
            augmentedFaceNodes[0].setFaceRegionsRenderable(modelRenderable);
            augmentedFaceNodes[0].setFaceMeshTexture(texture);
        }
    }

    public void trigger2(View v) {
        trigger2 = !trigger2;
        if (!trigger2) {
            augmentedFaceNodes[1].setFaceMeshTexture(null);
            augmentedFaceNodes[1].setFaceRegionsRenderable(null);
        } else {
            augmentedFaceNodes[1].setFaceRegionsRenderable(modelRenderable1);
        }
    }

    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}