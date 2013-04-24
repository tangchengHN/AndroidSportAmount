package com.jsu.tangcheng.androidsportamount.util;                                     //引用来自package com.pangff;    


/*
 * 该类为静态工具类，提供静态方法来计算
 * 小球应该的运动方向
 */
public class RotateUtil{                        
        //angle为弧度 gVector  为重力向量[x,y,z]
        //返回值为旋转后的向量
        public static double[] XRotate(double angle,double[] gVector){
                double[][] matrix={//绕x轴旋转变换矩阵          
                   {1,0,0},
                   {0,Math.cos(angle),Math.sin(angle)},                  
                   {0,-Math.sin(angle),Math.cos(angle)}
                };              
                double[] tempDot={gVector[0],gVector[1],gVector[2]};
                for(int j=0;j<3;j++){
                        gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
                                     tempDot[2]*matrix[2][j]);    
                }               
                return gVector;
        }
        
        //angle为弧度 gVector  为重力向量[x,y,z]
        //返回值为旋转后的向量
        public static double[] YRotate(double angle,double[] gVector){
                double[][] matrix={//绕y轴旋转变换矩阵          
                   {Math.cos(angle),0,-Math.sin(angle)},
                   {0,1,0},
                   {Math.sin(angle),0,Math.cos(angle)}
                };              
                double[] tempDot={gVector[0],gVector[1],gVector[2]};
                for(int j=0;j<3;j++){
                        gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
                                     tempDot[2]*matrix[2][j]);    
                }               
                return gVector;
        }               
        
        //angle为弧度 gVector  为重力向量[x,y,z]
        //返回值为旋转后的向量
        public static double[] ZRotate(double angle,double[] gVector){
                double[][] matrix={//绕z轴旋转变换矩阵          
                   {Math.cos(angle),Math.sin(angle),0},                  
                   {-Math.sin(angle),Math.cos(angle),0},
                   {0,0,1}   
                };              
                double[] tempDot={gVector[0],gVector[1],gVector[2]};
                for(int j=0;j<3;j++){
                        gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
                                     tempDot[2]*matrix[2][j]);    
                }               
                return gVector;
        }  
}