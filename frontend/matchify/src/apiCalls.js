export const postCall = async (url, body) => {
    try {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        };

        if (localStorage.getItem('token')) {
            options.headers['Authorization'] = `Bearer ${localStorage.getItem('token')}`;
        }

        const response = await fetch(url, options);
        return response;
    } catch (error) {
        console.error('Error:', error);
    }
}

export const getCall = async (url) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) {
        const responseData = response;
        return responseData;
      } else {
        console.error('Failed to fetch data');
      }
    } catch (error) {
      console.error('Error in API Call:', error);
    }
}