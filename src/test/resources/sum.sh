# Sums command line arguments
for i do
    sum=$(expr $sum + $i)
done
echo $sum