package org.njupt.njuptphysim;


import com.alibaba.druid.sql.visitor.functions.Char;
import com.sun.source.tree.Tree;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.server.controller.stu.StuController;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Test {

    private int[] ints;


     public class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode() {}
     TreeNode(int val) { this.val = val; }
     TreeNode(int val, TreeNode left, TreeNode right) {
         this.val = val;
         this.left = left;
         this.right = right;
        }
     }

    public class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int x) {
            val = x;
            next = null;
        }
    }


    public int digitFrequencyScore(int n) {
        HashMap<Integer,Integer> map = new HashMap<>();
        while (n>0) {
            int num = n%10;
            if (map.containsKey(num)){
                map.put(num,map.get(num)+1);
            } else {
                map.put(num,1);
            }
            n/=10;
        }

        return map.entrySet().stream()
                .mapToInt(entry -> entry.getValue()*entry.getKey())
                .sum();
    }


    public int totalWaviness(int num1, int num2) {
        if (num2<100) return 0;
        int start = Math.max(num1, 100);
        int sum = 0;
        for (int i = start; i <= num2; i++) {
            sum += solve(i);
        }
        return sum;
    }

    public int solve(int num){
        char[] array = String.valueOf(num).toCharArray();
        int ans = 0;
        for (int i = 1; i < array.length - 1; i++) {
            if (array[i] > Math.max(array[i-1],array[i+1]) || array[i] < Math.min(array[i-1], array[i+1])) {
                ans++;
            }
        }
        return ans;
    }



    public static boolean consecutiveSetBits(int n) {
        char[] chars = Integer.toBinaryString(n).toCharArray();
        int flag = 0;
        for (int i = 0; i < chars.length-1; i++) {
            if (chars[i]=='1' && chars[i+1]=='1') {
                if (flag == 1) {
                    flag = 0;
                    break;
                }
                flag = 1;
            }
        }
        return flag == 1;
    }


    private static final String[] list = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
    public String toHex(int num) {
        long curNum = num & 0xFFFFFFFFL;
        StringBuilder builder = new StringBuilder();
        while (curNum > 0) {
            builder.append(list[(int) (curNum % 16)]);
            curNum /= 16;
        }
        return builder.reverse().toString();
    }

    public int countSegments(String s) {
        int ans = 0, count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                if (count > 1) {
                    ans++;
                }
                count = 0;
            } else {
                count++;
            }
        }
        if (count>1) ans++;
        return ans;
    }


    public double angleClock(int hour, int minutes) {
        int hourAngle = hour * 30;
        int minutesAngle = minutes * 6;
        int delta = Math.abs(hourAngle - minutesAngle);
        return Math.min(delta, 360 - delta);
    }

    public int maxBuilding(int n, int[][] restrictions) {
        int len = restrictions.length;
        if (len == 0) return n-1;
        int[] h = new int[len];
        Arrays.sort(restrictions, Comparator.comparingInt(a -> a[0]));
        h[0] = Math.min(restrictions[0][0]-1, restrictions[0][1]);
        for (int i = 1; i < len; i++) {
            h[i] = Math.min(restrictions[i][1], restrictions[i][0] - restrictions[i-1][0] + h[i-1]);
        }
        for (int i = len-2; i >= 0; i--) {
            h[i] = Math.min(h[i], restrictions[i+1][0] - restrictions[i][0] + h[i+1]);
        }

        int ans = Math.max((restrictions[0][0] + h[0])/2, len - restrictions[len-1][0] + h[len-1]);
        for (int i = 1; i < len; i++) {
            ans = Math.max(ans, (h[i] + h[i-1] + restrictions[i][0] - restrictions[i-1][0])/2);
        }
        return ans;
    }

    public int minLights(int[] lights) {
        int n = lights.length;
        int[] diff = new int[n + 1];
        for (int i = 0; i < n; i++) {
            if (lights[i] > 0) {
                int left = Math.max(0, i - lights[i]);
                int right = Math.min(n - 1, i + lights[i]);
                diff[left]++;
                diff[right + 1]--;
            }
        }

        int ans = 0;
        int cur = 0;
        int unlitLen = 0;
        for (int i = 0; i < n; i++) {
            cur += diff[i];
            if (cur > 0) {
                ans += (unlitLen + 2) / 3;
                unlitLen = 0;
            } else {
                unlitLen++;
            }
        }
        ans += (unlitLen + 2) / 3;
        return ans;
    }



    public static void main(String[] args) {


    }

}
