package dxw405.util;


public interface RandomGenerator
{
	String[] POSTAL_SUFFIXES = {"Road", "Lane", "Way", "Street"};

	String generate();


	/**
	 * Generates a random string of the given length
	 *
	 * @param n Desired string length
	 * @return A String of n random characters
	 */
	static String randomString(int n)
	{
		char[] acc = new char[n];
		for (int i = 0; i < n; i++)
			acc[i] = (char) (Utils.RANDOM.nextInt(26) + 'a');

		return new String(acc);
	}

	class RandomBhamEmail implements RandomGenerator
	{
		@Override
		public String generate()
		{
			return randomString(6) + "@bham.ac.uk";
		}
	}

	class RandomEmail implements RandomGenerator
	{
		@Override
		public String generate()
		{
			return randomString(6) + "@" + randomString(6) + ".com";
		}
	}

	class RandomOffice implements RandomGenerator
	{

		@Override
		public String generate()
		{
			int floor = Utils.RANDOM.nextInt(5) + 1;
			int room = Utils.RANDOM.nextInt(300) + 50;

			return String.format("%d-%03d", floor, room);
		}
	}

	class RandomAddress implements RandomGenerator
	{

		@Override
		public String generate()
		{
			return String.format("%03d %s %s", Utils.RANDOM.nextInt(1000),
					randomString(5),
					POSTAL_SUFFIXES[Utils.RANDOM.nextInt(POSTAL_SUFFIXES.length)]);
		}
	}
}